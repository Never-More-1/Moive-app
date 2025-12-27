package movieApp.controller;

import movieApp.model.dto.tmdbDto.TmdbResponse;
import movieApp.service.TmdbService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/tmdb")
public class TmdbController {

    private final TmdbService tmdbApiService;

    public TmdbController(TmdbService tmdbApiService) {
        this.tmdbApiService = tmdbApiService;
    }

    // Получить информацию о фильме
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<?> getMovie(@PathVariable Integer movieId) {
        TmdbResponse movie = tmdbApiService.getMovieById(movieId);

        if (movie == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Фильм не найден");
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(movie);
    }

    // Проверить существует ли фильм
    @GetMapping("/movie/{movieId}/exists")
    public ResponseEntity<?> checkMovieExists(@PathVariable Integer movieId) {
        boolean exists = tmdbApiService.movieExists(movieId);

        Map<String, Object> response = new HashMap<>();
        response.put("movieId", movieId);
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }

    // Поиск фильмов
    @GetMapping("/search")
    public ResponseEntity<?> searchMovies(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "1") Integer page) {

        String results = tmdbApiService.searchMovies(query, page);

        if (results == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Ошибка поиска");
            return ResponseEntity.internalServerError().body(error);
        }

        return ResponseEntity.ok(results);
    }

    // Популярные фильмы
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularMovies(
            @RequestParam(required = false, defaultValue = "1") Integer page) {

        String results = tmdbApiService.getPopularMovies(page);

        if (results == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Ошибка получения данных");
            return ResponseEntity.internalServerError().body(error);
        }

        return ResponseEntity.ok(results);
    }

    // Тестовый эндпоинт для проверки подключения
    @GetMapping("/test")
    public ResponseEntity<?> testConnection() {
        try {
            // Попробуем получить известный фильм (Бойцовский клуб)
            TmdbResponse movie = tmdbApiService.getMovieById(550);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "TMDB API подключен");

            if (movie != null) {
                response.put("testMovie", movie.getTitle());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Ошибка подключения к TMDB API: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
