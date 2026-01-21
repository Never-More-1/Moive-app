package movieApp.security;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import movieApp.exception.UsernameExistsException;
import movieApp.exception.WrongPasswordException;
import movieApp.model.Role;
import movieApp.model.Security;
import movieApp.model.User;
import movieApp.model.dto.authDto.AuthRequest;
import movieApp.model.dto.authDto.AuthResponse;
import movieApp.model.dto.userDto.UserRegistrationDto;
import movieApp.service.UserService;
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
public class SecurityController {

    private final SecurityService securityService;
    private final UserService userService;

    public SecurityController(SecurityService securityService, UserService userService) {
        this.securityService = securityService;
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<Security>> getAllUsers() {
        List<Security> allUsers = securityService.getAllUsers();
        if(allUsers.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(allUsers);
    }

    @PostMapping("/jwt")
    public ResponseEntity<AuthResponse> generateJwt(@RequestBody AuthRequest authRequest) throws WrongPasswordException {
        if (authRequest == null || authRequest.getUsername() == null || authRequest.getPassword() == null) {
            throw new ValidationException("Invalid request");
        }

        Optional<String> jwt = securityService.generateJwt(authRequest);
        if (jwt.isPresent()) {
            return new ResponseEntity<>(new AuthResponse(jwt.get()), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Security> getSecurityById(@PathVariable("id") int id) {
        Optional<Security> security = securityService.getSecurityById(id);
        if (security.isPresent()) { // Исправлено: isPresent() вместо isEmpty()
            return new ResponseEntity<>(security.get(), HttpStatus.OK);
        }
        return ResponseEntity.notFound().build(); // Исправлено: 404 если не найден
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<Security>> getAllSecuritiesByRole(@PathVariable("role") String role) {
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
    public ResponseEntity<HttpStatus> registration(@Valid @RequestBody UserRegistrationDto userRegistrationDto,
                                                   BindingResult bindingResult) throws UsernameExistsException {
        if (bindingResult.hasErrors()) {
            List<String> errMessages = new ArrayList<>();

            for (ObjectError objectError : bindingResult.getAllErrors()) {
                //log.warn(objectError.toString());
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
    @PostMapping("/{id}/admin")
    public ResponseEntity<HttpStatus> setRoleToAdmin(@PathVariable Integer id) {
        if (securityService.setRoleToAdmin(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}