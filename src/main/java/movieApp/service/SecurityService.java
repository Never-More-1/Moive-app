package movieApp.service;

import org.springframework.stereotype.Service;
import movieApp.repository.UserRepository;

import java.sql.SQLException;

@Service
public class SecurityService {
    public UserRepository userRepository;

    public SecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    public boolean registration(UserRegistrationDto userRegistrationDto) throws SQLException, UsernameExistException {
//        if(isUsernameUsed(userRegistrationDto.getUsername())) {
//            throw new UsernameExistException(userRegistrationDto.getUsername());
//        }
//        User user = new User();
//        //user.setName(userRegistrationDto.getUsername());
//        user.setAge(userRegistrationDto.getAge());
//        user.setCreated(LocalDateTime.now());
//        try {
//            //return userRepository.addUser(user);
//            return false;
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        return false;
//    }

    public boolean isUsernameUsed(String username) throws SQLException {
        return userRepository.getUserByUsername(username).isPresent();
    }
}
