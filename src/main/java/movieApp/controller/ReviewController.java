package movieApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import movieApp.exception.UserNotFoundException;
import movieApp.model.Review;
import movieApp.model.dto.reviewDto.ReviewCreateDto;
import movieApp.model.dto.reviewDto.ReviewUpdateDto;
import movieApp.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@Tag(
        name = "Управление отзывами",
        description = "Операции с отзывами на фильмы: создание, чтение, обновление, удаление"
)
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // CREATE
    @PostMapping
    @Operation(
            summary = "Создать новый отзыв",
            description = "Создание отзыва на фильм. Требуется авторизация."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Отзыв успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация")
    })
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewCreateDto reviewDto) {
        try {
            Review createdReview = reviewService.addReview(reviewDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // READ
    @GetMapping("/myreviews")
    @Operation(
            summary = "Получить мои отзывы",
            description = "Получение всех отзывов текущего авторизованного пользователя"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отзывы найдены"),
            @ApiResponse(responseCode = "204", description = "Отзывы отсутствуют"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация")
    })
    public ResponseEntity<List<Review>> getMyReviews() {
        try {
            List<Review> reviews = reviewService.getMyReviews();
            if (reviews.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(reviews, HttpStatus.OK);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/user/{username}")
    @Operation(
            summary = "Получить отзывы пользователя",
            description = "Получение всех отзывов по имени пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отзывы найдены"),
            @ApiResponse(responseCode = "204", description = "Отзывы отсутствуют"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<List<Review>> getUserReviews(
            @Parameter(description = "Имя пользователя", example = "dante", required = true)
            @PathVariable("username") String username) {
        try {
            List<Review> reviews = reviewService.getReviewsByUsername(username);
            if (reviews.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(reviews, HttpStatus.OK);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/film/{filmTitle}")
    @Operation(
            summary = "Получить отзывы на фильм",
            description = "Получение всех отзывов по названию фильма"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отзывы найдены"),
            @ApiResponse(responseCode = "204", description = "Отзывы отсутствуют"),
            @ApiResponse(responseCode = "404", description = "Фильм не найден")
    })
    public ResponseEntity<List<Review>> getFilmReviews(
            @Parameter(description = "Название фильма", example = "Интерстеллар", required = true)
            @PathVariable("filmTitle") String filmTitle) {
        try {
            List<Review> reviews = reviewService.getReviewsByFilmTitle(filmTitle);
            return reviews.isEmpty() ?
                    ResponseEntity.noContent().build() :
                    ResponseEntity.ok(reviews);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/film/{filmId}/rating")
    @Operation(
            summary = "Получить рейтинг фильма",
            description = "Получение среднего рейтинга фильма по его ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Рейтинг найден"),
            @ApiResponse(responseCode = "404", description = "Фильм не найден")
    })
    public ResponseEntity<Double> getFilmRating(
            @Parameter(description = "ID фильма", example = "9", required = true)
            @PathVariable("filmId") int filmId) {
        try {
            Double rating = reviewService.getAverageRatingByFilmId(filmId);
            return ResponseEntity.ok(rating);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0.0);
        }
    }

    @GetMapping("/user/{username}/count")
    @Operation(
            summary = "Получить количество отзывов пользователя",
            description = "Получение общего количества отзывов пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Количество найдено"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<Integer> getUserReviewCount(
            @Parameter(description = "Имя пользователя", example = "john_doe", required = true)
            @PathVariable("username") String username) {
        try {
            int count = reviewService.getUserReviewCount(username);
            return ResponseEntity.ok(count);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // UPDATE
    @PutMapping("/film/{filmTitle}")
    @Operation(
            summary = "Обновить отзыв на фильм",
            description = "Обновление отзыва текущего пользователя на указанный фильм"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отзыв обновлен"),
            @ApiResponse(responseCode = "404", description = "Отзыв не найден"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация")
    })
    public ResponseEntity<Review> updateReviewByUserAndFilm(
            @Parameter(description = "Название фильма", example = "Интерстеллар", required = true)
            @PathVariable("filmTitle") String filmTitle,
            @Valid @RequestBody ReviewUpdateDto reviewUpdate) {
        try {
            Review updatedReview = reviewService.updateReview(filmTitle, reviewUpdate);
            return ResponseEntity.ok(updatedReview);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE
    @DeleteMapping("/film/{filmTitle}")
    @Operation(
            summary = "Удалить отзыв на фильм",
            description = "Удаление отзыва текущего пользователя на указанный фильм"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отзыв удален"),
            @ApiResponse(responseCode = "404", description = "Отзыв не найден"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация")
    })
    public ResponseEntity<String> deleteReviewByUserAndFilm(
            @Parameter(description = "Название фильма", example = "Интерстеллар", required = true)
            @PathVariable("filmTitle") String filmTitle) {
        boolean deleted = reviewService.deleteReview(filmTitle);
        return deleted ?
                ResponseEntity.ok("Отзыв удален") :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Отзыв не найден");
    }
}