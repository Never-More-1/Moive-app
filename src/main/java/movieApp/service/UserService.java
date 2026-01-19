package movieApp.service;

import movieApp.exception.UserNotFoundException;
import movieApp.model.Security;
import movieApp.model.User;
import movieApp.model.dto.userDto.UserCreateDto;
import movieApp.model.dto.userDto.UserUpdateDto;
import movieApp.repository.SecurityRepository;
import movieApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private SecurityRepository securityRepository;

    public UserService(SecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
    }

    //create
    public User addUser(UserCreateDto userDto) throws SQLException {
        User newUser = new User();
        newUser.setUsername(userDto.getUsername());
        newUser.setAge(userDto.getAge());

        return userRepository.save(newUser);
    }

    //read
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    public Optional<User> getMyself() {
        String userLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Security> userSecurity = securityRepository.getByUsername(userLogin);
        if (userSecurity.isPresent()) {
            return userRepository.findById(userSecurity.get().getUser().getId());
        }
        return Optional.empty();
    }

    //update
    public User updateUserById(int id, UserUpdateDto userUpdateDto) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        existingUser.setUsername(userUpdateDto.getUsername());
        existingUser.setAge(userUpdateDto.getAge());

        return userRepository.save(existingUser);
    }

    //delete
    public boolean removeUserById(int id) throws SQLException {
        if (getUserById(id).isEmpty()) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
        Optional<User> userFromDb = getUserById(id);
        return userFromDb.isEmpty();
    }
}
