package movieApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import movieApp.exception.FilmNotFoundException;
import movieApp.model.Film;
import movieApp.model.dto.filmDto.FilmCreateDto;
import movieApp.model.dto.filmDto.FilmUpdateDto;
import movieApp.service.FilmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/films")
@Tag(
        name = "Управление фильмами",
        description = "Операции с фильмами: просмотр, добавление, обновление, удаление"
)
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    // CREATE
    @PostMapping("/create")
    @Operation(
            summary = "Добавить новый фильм",
            description = "Добавление фильма на сайт (требуются права ADMIN)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Фильм успешно добавлен"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные фильма"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (требуется роль ADMIN)")
    })
    public ResponseEntity<Film> addFilm(@Valid @RequestBody FilmCreateDto filmCreateDto) {
        Film createdFilm = filmService.addFilm(filmCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    // READ
    @GetMapping
    @Operation(
            summary = "Получить все фильмы",
            description = "Получение списка всех фильмов на сайте"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список фильмов получен"),
            @ApiResponse(responseCode = "204", description = "Фильмы не найдены")
    })
    public ResponseEntity<List<Film>> getAllFilms() {
        List<Film> films = filmService.getAllFilms();
        if (films.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(films);
    }

    @GetMapping("/{title}")
    @Operation(
            summary = "Найти фильм по названию",
            description = "Поиск фильма по точному названию"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Фильм найден"),
            @ApiResponse(responseCode = "404", description = "Фильм не найден")
    })
    public ResponseEntity<Film> getFilm(
            @Parameter(
                    description = "Название фильма",
                    example = "Интерстеллар",
                    required = true
            )
            @PathVariable("title") String title) {
        try {
            Film film = filmService.getFilmByTitle(title);
            return ResponseEntity.ok(film);
        } catch (FilmNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // UPDATE
    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить информацию о фильме",
            description = "Обновление данных фильма: название, год выхода, режиссер, рейтинг (требуются права ADMIN)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Фильм успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (требуется роль ADMIN)"),
            @ApiResponse(responseCode = "404", description = "Фильм не найден")
    })
    public ResponseEntity<Film> updateFilm(
            @Parameter(
                    description = "ID фильма",
                    example = "123",
                    required = true
            )
            @PathVariable("id") int filmId,
            @Valid @RequestBody FilmUpdateDto filmUpdateDto) {
        Film updatedFilm = filmService.updateFilm(filmId, filmUpdateDto);
        return ResponseEntity.ok(updatedFilm);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить фильм",
            description = "Удаление фильма с сайта (требуются права ADMIN)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Фильм успешно удален"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (требуется роль ADMIN)"),
            @ApiResponse(responseCode = "404", description = "Фильм не найден")
    })
    public ResponseEntity<?> deleteFilm(
            @Parameter(
                    description = "ID фильма",
                    example = "123",
                    required = true
            )
            @PathVariable("id") int filmId) {
        try {
            Film deletedFilm = filmService.deleteFilm(filmId);
            return ResponseEntity.ok(deletedFilm);
        } catch (FilmNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}