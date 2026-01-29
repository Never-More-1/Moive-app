package movieApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import movieApp.exception.UserNotFoundException;
import movieApp.model.dto.userDto.UserUpdateDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import movieApp.model.User;
import movieApp.service.UserService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@Tag(
        name = "Users",
        description = "System user management"
)
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    @Operation(
            summary = "Get all users",
            description = "Getting a list of all registered users"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User list received"),
            @ApiResponse(responseCode = "204", description = "No users found"),
            @ApiResponse(responseCode = "401", description = "Authorization required"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights")
    })
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        if (allUsers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/{username}")
    @Operation(
            summary = "Get user by name",
            description = "Search for a user by username"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "401", description = "Authorization required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> getUserByUsername(
            @Parameter(
                    description = "Username",
                    example = "dante",
                    required = true
            )
            @PathVariable("username") String username) {
        Optional<User> user = userService.getUserByUsername(username);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/myself")
    @Operation(
            summary = "Get information about yourself",
            description = "Getting information about the currently logged in user"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Information received"),
            @ApiResponse(responseCode = "401", description = "Authorization required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> getMyself() {
        Optional<User> user = userService.getMyself();
        if (user.isEmpty()) {
            throw new UserNotFoundException("Current user not found");
        }
        return ResponseEntity.ok(user.get());
    }

    @PutMapping("/{username}")
    @Operation(
            summary = "Update user data",
            description = "Updating user information by username"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details updated"),
            @ApiResponse(responseCode = "400", description = "Incorrect data"),
            @ApiResponse(responseCode = "401", description = "Authorization required"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> updateUserByUsername(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User update data",
                    required = true
            )
            @RequestBody UserUpdateDto userUpdateDto,
            @Parameter(
                    description = "Username",
                    example = "dante",
                    required = true
            )
            @PathVariable("username") String username) {
        try {
            User updatedUser = userService.updateUser(username, userUpdateDto);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{username}")
    @Operation(
            summary = "Delete user",
            description = "Deleting a user by username"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "401", description = "Authorization required"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Conflict during deletion")
    })
    public ResponseEntity<HttpStatusCode> deleteUserByUsername(
            @Parameter(
                    description = "Username",
                    example = "test_user_1",
                    required = true
            )
            @PathVariable("username") String username) {
        if (userService.removeUserByUsername(username)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}