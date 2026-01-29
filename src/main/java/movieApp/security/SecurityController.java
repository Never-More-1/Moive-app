package movieApp.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import movieApp.exception.UsernameExistsException;
import movieApp.exception.WrongPasswordException;
import movieApp.model.Role;
import movieApp.model.Security;
import movieApp.model.dto.authDto.AuthRequest;
import movieApp.model.dto.authDto.AuthResponse;
import movieApp.model.dto.userDto.UserRegistrationDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/security")
@Tag(
        name = "Authentication and Security",
        description = "Registration, authorization and user access management"
)
public class SecurityController {

    private final SecurityService securityService;

    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping()
    @Operation(
            summary = "Get all security users",
            description = "Getting a list of all user security records"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List received"),
            @ApiResponse(responseCode = "204", description = "No records found"),
            @ApiResponse(responseCode = "401", description = "Authorization required"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights (requires ADMIN role)")
    })
    public ResponseEntity<List<Security>> getAllUsers() {
        List<Security> allUsers = securityService.getAllUsers();
        if(allUsers.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(allUsers);
    }

    @PostMapping("/jwt")
    @Operation(
            summary = "Get a JWT token",
            description = "Authenticate the user and obtain a JWT token to access the API"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The token was successfully generated",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Incorrect credentials")
    })
    @ResponseBody
    public ResponseEntity<AuthResponse> generateJwt(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Authentication credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuthRequest.class))
            )
            @RequestBody AuthRequest authRequest) throws WrongPasswordException {

        if (authRequest == null || authRequest.getUsername() == null || authRequest.getPassword() == null) {
            throw new ValidationException("Invalid request");
        }
        Optional<String> jwt = securityService.generateJwt(authRequest);
        if (jwt.isPresent()) {
            return ResponseEntity.ok(new AuthResponse(jwt.get()));
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{username}")
    @Operation(
            summary = "Get security record by ID",
            description = "Obtaining user security information by username"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entry found"),
            @ApiResponse(responseCode = "401", description = "Authorization required"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights (requires ADMIN role)"),
            @ApiResponse(responseCode = "404", description = "Record not found")
    })
    public ResponseEntity<Security> getSecurityByUsername(
            @Parameter(
                    description = "Security entry username",
                    example = "dante",
                    required = true
            )
            @PathVariable("username") String username) {
        Optional<Security> security = securityService.getSecurityByUsername(username);
        if (security.isPresent()) {
            return new ResponseEntity<>(security.get(), HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("role/{role}")
    @Operation(
            summary = "Get users by role",
            description = "Getting a list of security users by role (ADMIN or USER)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List received"),
            @ApiResponse(responseCode = "400", description = "Incorrect role"),
            @ApiResponse(responseCode = "401", description = "Authorization required"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights"),
            @ApiResponse(responseCode = "404", description = "No users found")
    })
    public ResponseEntity<List<Security>> getAllSecuritiesByRole(
            @Parameter(
                    description = "User Role",
                    example = "ADMIN",
                    required = true
            )
            @PathVariable("role") String role) {
        try {
            role = role.toUpperCase();
            Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Security> allSecuritiesByRole = securityService.getAllSecuritiesByRole(role);
        if (!allSecuritiesByRole.isEmpty()) {
            return new ResponseEntity<>(allSecuritiesByRole, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/registration")
    @Operation(
            summary = "New user registration",
            description = "Creating a new user account"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Incorrect data or validation errors"),
            @ApiResponse(responseCode = "409", description = "A user with this name already exists")
    })
    public ResponseEntity<HttpStatus> registration(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New user registration details",
                    required = true
            )
            @Valid @RequestBody UserRegistrationDto userRegistrationDto,
            BindingResult bindingResult) throws UsernameExistsException {
        if (bindingResult.hasErrors()) {
            List<String> errMessages = new ArrayList<>();

            for (ObjectError objectError : bindingResult.getAllErrors()) {
                errMessages.add(objectError.getDefaultMessage());
            }
            throw new ValidationException(String.valueOf(errMessages));
        }
        if (securityService.registration(userRegistrationDto)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{username}")
    @Operation(
            summary = "Assign the ADMIN role",
            description = "Assigning the ADMIN role to a user by username (for ADMIN only)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The role has been successfully assigned"),
            @ApiResponse(responseCode = "401", description = "Authorization required"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights (requires ADMIN role)"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Conflict in role assignment")
    })
    public ResponseEntity<HttpStatus> setRoleToAdmin(
            @Parameter(
                    description = "Username",
                    example = "dante",
                    required = true
            )
            @PathVariable String username) {
        if (securityService.setRoleToModerator(username)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}