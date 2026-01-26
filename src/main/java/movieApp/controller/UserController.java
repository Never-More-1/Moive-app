package movieApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import movieApp.exception.UserNotFoundException;
import movieApp.model.Security;
import movieApp.model.dto.userDto.UserUpdateDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import movieApp.model.User;
import movieApp.service.UserService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@Tag(
        name = "Пользователи",
        description = "Управление пользователями системы"
)
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    @Operation(
            summary = "Получить всех пользователей",
            description = "Получение списка всех зарегистрированных пользователей"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей получен"),
            @ApiResponse(responseCode = "204", description = "Пользователи не найдены"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        if(allUsers.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(allUsers);
    }

    //read
    @GetMapping("/username/{username}")
    @Operation(
            summary = "Получить пользователя по имени",
            description = "Поиск пользователя по имени пользователя (username)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<User> getUserByUsername(
            @Parameter(
                    description = "Имя пользователя",
                    example = "dante",
                    required = true
            )
            @PathVariable("username") String username) {
        Optional<User> user = userService.getUserByUsername(username);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/myself")
    @Operation(
            summary = "Получить информацию о себе",
            description = "Получение информации о текущем авторизованном пользователе"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация получена"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<User> getMyself() {
        Optional<User> user = userService.getMyself();
        if (user.isEmpty()) {
            throw new UserNotFoundException("Текущий пользователь не найден");
        }
        return ResponseEntity.ok(user.get());
    }

    //update
    @PutMapping("/username/{username}")
    @Operation(
            summary = "Обновить данные пользователя",
            description = "Обновление информации о пользователе по имени пользователя"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные пользователя обновлены"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<?> updateUserByUsername(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для обновления пользователя",
                    required = true
            )
            @RequestBody UserUpdateDto userUpdateDto,
            @Parameter(
                    description = "Имя пользователя",
                    example = "dante",
                    required = true
            )
            @PathVariable("username") String username) {
        try {
            User updatedUser = userService.updateUser(username, userUpdateDto);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //delete
    @DeleteMapping("/username/{username}")
    @Operation(
            summary = "Удалить пользователя",
            description = "Удаление пользователя по имени пользователя"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь удален"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "409", description = "Конфликт при удалении")
    })
    public ResponseEntity<HttpStatusCode> deleteUserByUsername(
            @Parameter(
                    description = "Имя пользователя",
                    example = "dante",
                    required = true
            )
            @PathVariable("username") String username) throws SQLException {
        if (userService.removeUserByUsername(username)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}