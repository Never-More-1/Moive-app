package movieApp.service;

import jakarta.transaction.Transactional;
import movieApp.exception.UserNotFoundException;
import movieApp.model.Role;
import movieApp.model.Security;
import movieApp.model.User;
import movieApp.model.dto.userDto.UserCreateDto;
import movieApp.model.dto.userDto.UserUpdateDto;
import movieApp.repository.FavoriteRepository;
import movieApp.repository.SecurityRepository;
import movieApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityRepository securityRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //read
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByUsername(String username) {
        Optional<Security> userSecurity = securityRepository.getByUsername(username);
        if (userSecurity.isPresent()) {
            return userRepository.findById(userSecurity.get().getUser().getId());
        }
        return Optional.empty();
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
    @Transactional
    public User updateUser(String username, UserUpdateDto userUpdateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<Security> currentUserSecurity = securityRepository.getByUsername(username);

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Пользователь не аутентифицирован");
        }

        Security currentUserSec = currentUserSecurity.get();
        boolean isAdmin = currentUserSec.getRole() == Role.ADMIN;
        boolean isOwner = currentUsername.equals(username);

        if (!isAdmin && !isOwner) {
            throw new SecurityException("У вас нет прав, чтобы обновлять этого пользователя");
        }

        Optional<Security> securityOptional = securityRepository.getByUsername(username);
        if (securityOptional.isEmpty()) {
            throw new UserNotFoundException(username);
        }

        Security security = securityOptional.get();
        User user = security.getUser();

        String newUsername = userUpdateDto.getUsername();
        if (newUsername != null && !newUsername.equals(username)) {
            Optional<Security> existingSecurity = securityRepository.getByUsername(newUsername);
            if (existingSecurity.isPresent()) {
                throw new IllegalArgumentException("Username " + newUsername + " уже занят");
            }
        }

        user.setUsername(userUpdateDto.getUsername());
        user.setAge(userUpdateDto.getAge());
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        security.setUsername(userUpdateDto.getUsername());
        security.setAge(userUpdateDto.getAge());
        security.setEmail(userUpdateDto.getEmail());

        if (userUpdateDto.getPassword() != null && !userUpdateDto.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(userUpdateDto.getPassword());
            security.setPassword(encodedPassword);
        }

        security.setCreatedAt(LocalDateTime.now());
        securityRepository.save(security);

        return user;
    }

    //delete
    @Transactional
    public boolean removeUserByUsername(String username) throws SQLException {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Security> currentUserSecurity = securityRepository.getByUsername(currentUsername);

        if (currentUserSecurity.isEmpty()) {
            throw new SecurityException("Пользователь не аунтифицирован");
        }
        Security currentUserSec = currentUserSecurity.get();
        boolean isAdmin = currentUserSec.getRole() == Role.ADMIN;
        boolean isOwner = currentUsername.equals(username);

        if (!isAdmin && !isOwner) {
            throw new SecurityException("У вас нет прав, чтобы удалять этого пользователя");
        }
        Optional<Security> securityOptional = securityRepository.getByUsername(username);
        if (securityOptional.isEmpty()) {
            throw new UserNotFoundException(username);
        }
        Security security = securityOptional.get();
        User user = security.getUser();
        securityRepository.delete(security);
        userRepository.delete(user);
        return securityRepository.getByUsername(username).isEmpty() &&
                userRepository.findByUsername(username).isEmpty();
    }
}