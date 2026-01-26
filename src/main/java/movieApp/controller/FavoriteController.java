package movieApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import movieApp.model.Favorite;
import movieApp.model.dto.favoriteDto.FavoriteResponseDto;
import movieApp.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@Tag(
        name = "Избранное",
        description = "Управление избранными фильмами пользователей"
)
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // create
    @PostMapping("/film/{filmId}")
    @Operation(
            summary = "Добавить фильм в избранное",
            description = "Добавление фильма в список избранного текущего пользователя"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Фильм успешно добавлен в избранное"),
            @ApiResponse(responseCode = "400", description = "Фильм уже в избранном или не найден"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "404", description = "Фильм не найден")
    })
    public ResponseEntity<?> addToFavoritesWithPath(
            @Parameter(
                    description = "ID фильма",
                    example = "123",
                    required = true
            )
            @PathVariable Integer filmId) {
        try {
            Favorite favorite = favoriteService.addToFavorites(filmId);
            FavoriteResponseDto responseDto = FavoriteResponseDto.fromFavorite(favorite);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // read
    @GetMapping("/user/{username}")
    @Operation(
            summary = "Получить избранное пользователя",
            description = "Получение списка избранных фильмов указанного пользователя"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список избранного получен"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<?> getUserFavorites(
            @Parameter(
                    description = "Имя пользователя",
                    example = "john_doe",
                    required = true
            )
            @PathVariable String username) {
        try {
            List<Favorite> favorite = favoriteService.findUserFavoritesByUsername(username);
            return ResponseEntity.ok(favorite);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/count/{username}")
    @Operation(
            summary = "Количество избранных фильмов",
            description = "Получение количества избранных фильмов указанного пользователя"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Количество получено"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<?> countUserFavorites(
            @Parameter(
                    description = "Имя пользователя",
                    example = "john_doe",
                    required = true
            )
            @PathVariable String username) {
        try {
            int count = favoriteService.countUserFavorites(username);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // delete
    @DeleteMapping("/film/{filmId}")
    @Operation(
            summary = "Удалить фильм из избранного",
            description = "Удаление фильма из списка избранного текущего пользователя"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Фильм удален из избранного"),
            @ApiResponse(responseCode = "400", description = "Фильм не найден в избранном"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "404", description = "Фильм не найден")
    })
    public ResponseEntity<?> deleteFromFavorites(
            @Parameter(
                    description = "ID фильма",
                    example = "9",
                    required = true
            )
            @PathVariable Integer filmId) {
        try {
            favoriteService.deleteFromFavorites(filmId);
            return ResponseEntity.ok("Фильм удален из избранного");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}