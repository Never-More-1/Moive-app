package movieApp.service;

import movieApp.exception.UserNotFoundException;
import movieApp.model.User;
import movieApp.model.dto.userDto.UserCreateDto;
import movieApp.model.dto.userDto.UserUpdateDto;
import movieApp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    //Create
    public User addUser(UserCreateDto user) throws SQLException {
        User newUser = new User();
        return userRepository.save(newUser);
    }

    //Read
    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    //Update
    public User updateUserById(int id, UserUpdateDto userUpdateDto) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        existingUser.setUsername(userUpdateDto.getUsername());
        existingUser.setAge(userUpdateDto.getAge());
        existingUser.setRole(userUpdateDto.getRole());
        existingUser.setEmail(userUpdateDto.getEmail());

        return userRepository.save(existingUser);
    }

    //Delete
    public boolean removeUserById(int id) throws SQLException {
        if (getUserById(id).isEmpty()) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
        Optional<User> userFromDb = getUserById(id);
        return userFromDb.isEmpty();
    }
}
