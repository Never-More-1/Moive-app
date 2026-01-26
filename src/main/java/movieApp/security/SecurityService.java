package movieApp.security;

import movieApp.exception.UserNotFoundException;
import movieApp.exception.UsernameExistsException;
import movieApp.exception.WrongPasswordException;
import movieApp.model.Role;
import movieApp.model.Security;
import movieApp.model.User;
import movieApp.model.dto.authDto.AuthRequest;
import movieApp.model.dto.userDto.UserRegistrationDto;
import movieApp.repository.SecurityRepository;
import movieApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SecurityService {
    private final UserRepository userRepository;
    private final SecurityRepository securityRepository;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // Исправленный конструктор
    public SecurityService(UserRepository userRepository,
                           SecurityRepository securityRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.securityRepository = securityRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public List<Security> getAllUsers() {
        return securityRepository.findAll();
    }

    @Transactional(rollbackFor = {Exception.class},
            noRollbackFor = {UsernameExistsException.class},
            isolation = Isolation.READ_COMMITTED)
    public boolean registration(UserRegistrationDto userRegistrationDto) throws UsernameExistsException {
        //log.info("Registering user {}", userRegistrationDto.getUsername());

        if (isUsernameUsed(userRegistrationDto.getUsername())) {
            throw new UsernameExistsException(userRegistrationDto.getUsername());
        }

        try {
            User user = new User();
            user.setUsername(userRegistrationDto.getUsername());
            user.setAge(userRegistrationDto.getAge());
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);

            Security security = new Security();
            security.setUser(user);
            security.setUsername(userRegistrationDto.getUsername());
            security.setPassword(bCryptPasswordEncoder.encode(userRegistrationDto.getPassword()));
            security.setEmail(userRegistrationDto.getEmail());
            security.setAge(userRegistrationDto.getAge());
            security.setCreatedAt(LocalDateTime.now());

            security.setRole(Role.USER);
            securityRepository.save(security);
            return true;
        } catch (Exception e) {
            //log.error("Registration failed: {}", e.getMessage(), e);
            throw new RuntimeException("Registration failed", e);
        }
    }

    public Optional<Security> getSecurityById(int id) {
        return securityRepository.findById(id);
    }

    public Boolean setRoleToAdmin(int id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        return securityRepository.setAdminRoleByUserId(id) > 0;
    }

    public boolean isUsernameUsed(String username) {
        return securityRepository.existsByUsername(username);
    }

    public List<movieApp.model.Security> getAllSecuritiesByRole(String role) {
        return securityRepository.customFindByRole(role);
    }

    public Optional<String> generateJwt(AuthRequest request) throws WrongPasswordException {
        Optional<Security> security = securityRepository.getByUsername(request.getUsername());
        if (security.isEmpty()) {
            throw new UsernameNotFoundException(request.getUsername());
        }

        if (!bCryptPasswordEncoder.matches(request.getPassword(), security.get().getPassword())) {
            throw new WrongPasswordException(request.getPassword());
        }

        return Optional.ofNullable(jwtUtils.generateToken(security.get().getUsername()));
    }
}