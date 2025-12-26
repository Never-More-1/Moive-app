package movieApp.controller;

import jakarta.validation.Valid;
import movieApp.model.dto.UserUpdateDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import movieApp.model.User;
import movieApp.model.dto.UserCreateDto;
import movieApp.service.UserService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers(Model model) {
        List<User> allUsers = userService.getAllUsers();
        if(allUsers.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(allUsers);
    }

    //Create
    @PostMapping()
    public ResponseEntity<HttpStatusCode> addUser(@RequestBody UserCreateDto user) throws SQLException {
        if (userService.addUser(user)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    //Read
    @GetMapping("/id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") int id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable("username") String username) {
        Optional<User> user = userService.getUserByUsername(username);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    //Update
    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateUserById(@RequestBody UserUpdateDto user,
                                        @PathVariable("id") int id) throws SQLException {
        if (userService.updateUserById(user, id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PutMapping("/username/{username}")
    public ResponseEntity<?> updateUserByUsername(@RequestBody UserUpdateDto user,
                                                  @PathVariable("username") String username) throws SQLException {
        if (userService.updateUserByUsername(user, username)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    //Delete
    @DeleteMapping("/id/{id}")
    public ResponseEntity<HttpStatusCode> deleteUserById(@PathVariable("id") int id) throws SQLException {
        if (userService.removeUserById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @DeleteMapping("/username/{username}")
    public ResponseEntity<HttpStatusCode> deleteUserByUsername(@PathVariable("username") String username) throws SQLException {
        if (userService.removeUserByUsername(username)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
