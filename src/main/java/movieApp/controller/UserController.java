package movieApp.controller;

import movieApp.exception.UserNotFoundException;
import movieApp.model.dto.userDto.UserUpdateDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import movieApp.model.User;
import movieApp.model.dto.userDto.UserCreateDto;
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

    //Create
    @PostMapping()
    public ResponseEntity<HttpStatusCode> addUser(@RequestBody UserCreateDto user) throws SQLException {
        User savedUser = userService.addUser(user);
        if (savedUser != null) {
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

    //Update
    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateUserById(@RequestBody UserUpdateDto userUpdateDto,
                                            @PathVariable("id") int id) {
        try {
            User updatedUser = userService.updateUserById(id, userUpdateDto);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    //Delete
    @DeleteMapping("/id/{id}")
    public ResponseEntity<HttpStatusCode> deleteUserById(@PathVariable("id") int id) throws SQLException {
        if (userService.removeUserById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
