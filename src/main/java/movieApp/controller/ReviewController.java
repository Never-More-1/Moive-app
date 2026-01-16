package movieApp.controller;

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
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Все отзывы пользователя
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getUserReviews(@PathVariable("userId") int userId) {
        try {
            List<Review> reviews = reviewService.getReviewsByUserId(userId);
            return reviews.isEmpty() ?
                    ResponseEntity.noContent().build() :
                    ResponseEntity.ok(reviews);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Все отзывы на фильм
    @GetMapping("/film/{filmId}")
    public ResponseEntity<List<Review>> getFilmReviews(@PathVariable("filmId") int filmId) {
        try {
            List<Review> reviews = reviewService.getReviewsByFilmId(filmId);
            return reviews.isEmpty() ?
                    ResponseEntity.noContent().build() :
                    ResponseEntity.ok(reviews);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Можно ли создать отзыв?
    @GetMapping("/can-create/user/{userId}/film/{filmId}")
    public ResponseEntity<Boolean> canCreateReview(
            @PathVariable("userId") int userId,
            @PathVariable("filmId") int filmId) {
        boolean canCreate = reviewService.canUserCreateReview(userId, filmId);
        return ResponseEntity.ok(canCreate);
    }

    // Средний рейтинг фильма
    @GetMapping("/film/{filmId}/rating")
    public ResponseEntity<Double> getFilmRating(@PathVariable("filmId") int filmId) {
        try {
            Double rating = reviewService.getAverageRatingByFilmId(filmId);
            return ResponseEntity.ok(rating);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0.0);
        }
    }

    // Количество отзывов пользователя
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Integer> getUserReviewCount(@PathVariable("userId") int userId) {
        try {
            int count = reviewService.getUserReviewCount(userId);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
        }
    }

    // Создать отзыв
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody ReviewCreateDto reviewDto) {
        try {
            Review createdReview = reviewService.addReview(reviewDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Получить отзыв пользователя на фильм
    @GetMapping("/user/{userId}/film/{filmId}")
    public ResponseEntity<Review> getUserFilmReview(
            @PathVariable("userId") int userId,
            @PathVariable("filmId") int filmId) {
        return reviewService.getReviewByUserAndFilm(userId, filmId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Обновить отзыв по ID
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(
            @PathVariable("id") int id,
            @RequestBody ReviewUpdateDto reviewUpdate) {
        try {
            Review updatedReview = reviewService.updateReview(id, reviewUpdate);
            return ResponseEntity.ok(updatedReview);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Обновить отзыв по userId и filmId
    @PutMapping("/user/{userId}/film/{filmId}")
    public ResponseEntity<Review> updateReviewByUserAndFilm(
            @PathVariable("userId") int userId,
            @PathVariable("filmId") int filmId,
            @RequestBody ReviewUpdateDto reviewUpdate) {
        try {
            Review updatedReview = reviewService.updateReviewByUserAndFilm(userId, filmId, reviewUpdate);
            return ResponseEntity.ok(updatedReview);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Удалить отзыв по ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable("id") int id) {
        boolean deleted = reviewService.removeReviewById(id);
        return deleted ?
                ResponseEntity.ok("Отзыв удален") :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Отзыв не найден");
    }

    // Удалить отзыв по userId и filmId
    @DeleteMapping("/user/{userId}/film/{filmId}")
    public ResponseEntity<String> deleteReviewByUserAndFilm(
            @PathVariable("userId") int userId,
            @PathVariable("filmId") int filmId) {
        boolean deleted = reviewService.removeReviewByUserAndFilm(userId, filmId);
        return deleted ?
                ResponseEntity.ok("Отзыв удален") :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Отзыв не найден");
    }
}