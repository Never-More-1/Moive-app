package movieApp.service;

import movieApp.model.User;
import movieApp.model.dto.UserCreateDto;
import movieApp.model.dto.UserUpdateDto;
import movieApp.repository.UserRepository;
import org.springframework.stereotype.Service;

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
        return userRepository.getAllUsers();
    }

    //Create
    public boolean addUser(UserCreateDto user) throws SQLException {
        return userRepository.addUser(user);
    }

    //Read
    public Optional<User> getUserById(int id) {
        return userRepository.getUserById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    //Update
    public boolean updateUserById(UserUpdateDto user, int id) throws SQLException {
        return userRepository.updateUserById(user, id);
    }

    public boolean updateUserByUsername(UserUpdateDto user, String username) throws SQLException {
        return userRepository.updateUserByUsername(user, username);
    }

    //Delete
    public boolean removeUserById(int id) throws SQLException {
        Optional<User> userFromDb = getUserById(id);
        if (userFromDb.isPresent() && userRepository.removeUserById(id)) {
            userFromDb = getUserById(id);
            return userFromDb.isEmpty();
        }
        return false;
    }

    public boolean removeUserByUsername(String username) throws SQLException {
        Optional<User> userFromDb = getUserByUsername(username);
        if (userFromDb.isPresent() && userRepository.removeUserByUsername(username)) {
            userFromDb = getUserByUsername(username);
            return userFromDb.isEmpty();
        }
        return false;
    }
}
