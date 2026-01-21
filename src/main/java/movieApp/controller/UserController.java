package movieApp.controller;

import movieApp.exception.UserNotFoundException;
import movieApp.model.Security;
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
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        if(allUsers.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(allUsers);
    }

    //read
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable("username") String username) {
        Optional<User> user = userService.getUserByUsername(username);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/myself")
    public ResponseEntity<User> getMyself() {
        Optional<User> user = userService.getMyself();
        if (user.isEmpty()) {
           throw new UserNotFoundException(user.get().getUsername());
        }
        return ResponseEntity.ok(user.get());
    }

    //update
    @PutMapping("/username/{username}")
    public ResponseEntity<?> updateUserByUsername(@RequestBody UserUpdateDto userUpdateDto,
                                            @PathVariable("username") String username) {
        try {
            User updatedUser = userService.updateUserByUsername(username, userUpdateDto);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    //delete
    @DeleteMapping("/username/{username}")
    public ResponseEntity<HttpStatusCode> deleteUserByUsername(@PathVariable("username") String username) throws SQLException {
        if (userService.removeUserByUsername(username)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
