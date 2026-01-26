package movieApp.security;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import movieApp.exception.UsernameExistsException;
import movieApp.exception.WrongPasswordException;
import movieApp.model.Role;
import movieApp.model.Security;
import movieApp.model.dto.authDto.AuthRequest;
import movieApp.model.dto.authDto.AuthResponse;
import movieApp.model.dto.userDto.UserRegistrationDto;
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
@Tag(
        name = "Аутентификация и безопасность",
        description = "Регистрация, авторизация и управление доступом пользователей"
)
public class SecurityController {

    private final SecurityService securityService;

    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping()
    @Operation(
            summary = "Получить всех пользователей безопасности",
            description = "Получение списка всех записей безопасности пользователей"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список получен"),
            @ApiResponse(responseCode = "204", description = "Записи не найдены"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (требуется роль ADMIN)")
    })
    public ResponseEntity<List<Security>> getAllUsers() {
        List<Security> allUsers = securityService.getAllUsers();
        if(allUsers.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(allUsers);
    }

    @PostMapping("/jwt")
    @Operation(
            summary = "Получить JWT токен",
            description = "Аутентификация пользователя и получение JWT токена для доступа к API"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токен успешно сгенерирован",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    @ResponseBody
    public ResponseEntity<AuthResponse> generateJwt(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Учетные данные для аутентификации",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuthRequest.class))
            )
            @RequestBody AuthRequest authRequest) throws WrongPasswordException {

        if (authRequest == null || authRequest.getUsername() == null || authRequest.getPassword() == null) {
            throw new ValidationException("Invalid request");
        }

        Optional<String> jwt = securityService.generateJwt(authRequest);
        if (jwt.isPresent()) {
            // Возвращаем AuthResponse объект
            return ResponseEntity.ok(new AuthResponse(jwt.get()));
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(
            summary = "Получить запись безопасности по ID",
            description = "Получение информации о безопасности пользователя по ID"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запись найдена"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (требуется роль ADMIN)"),
            @ApiResponse(responseCode = "404", description = "Запись не найдена")
    })
    public ResponseEntity<Security> getSecurityById(
            @Parameter(
                    description = "ID записи безопасности",
                    example = "1",
                    required = true
            )
            @PathVariable("id") int id) {
        Optional<Security> security = securityService.getSecurityById(id);
        if (security.isPresent()) {
            return new ResponseEntity<>(security.get(), HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/role/{role}")
    @Operation(
            summary = "Получить пользователей по роли",
            description = "Получение списка пользователей безопасности по роли (ADMIN или USER)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список получен"),
            @ApiResponse(responseCode = "400", description = "Некорректная роль"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Пользователи не найдены")
    })
    public ResponseEntity<List<Security>> getAllSecuritiesByRole(
            @Parameter(
                    description = "Роль пользователя",
                    example = "ADMIN",
                    required = true
            )
            @PathVariable("role") String role) {
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
    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создание нового аккаунта пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные или ошибки валидации"),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким именем уже существует")
    })
    public ResponseEntity<HttpStatus> registration(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для регистрации нового пользователя",
                    required = true
            )
            @Valid @RequestBody UserRegistrationDto userRegistrationDto,
            BindingResult bindingResult) throws UsernameExistsException {
        if (bindingResult.hasErrors()) {
            List<String> errMessages = new ArrayList<>();

            for (ObjectError objectError : bindingResult.getAllErrors()) {
                errMessages.add(objectError.getDefaultMessage());
            }
            throw new ValidationException(String.valueOf(errMessages));
        }
        if (securityService.registration(userRegistrationDto)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/admin")
    @Operation(
            summary = "Назначить роль ADMIN",
            description = "Назначение роли ADMIN пользователю по ID (только для администраторов)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Роль успешно назначена"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (требуется роль ADMIN)"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "409", description = "Конфликт при назначении роли")
    })
    public ResponseEntity<HttpStatus> setRoleToAdmin(
            @Parameter(
                    description = "ID пользователя",
                    example = "1",
                    required = true
            )
            @PathVariable Integer id) {
        if (securityService.setRoleToAdmin(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}