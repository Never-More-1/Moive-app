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

    // create
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody ReviewCreateDto reviewDto) {
        try {
            Review createdReview = reviewService.addReview(reviewDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // read
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

    @GetMapping("/film/{filmId}/rating")
    public ResponseEntity<Double> getFilmRating(@PathVariable("filmId") int filmId) {
        try {
            Double rating = reviewService.getAverageRatingByFilmId(filmId);
            return ResponseEntity.ok(rating);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0.0);
        }
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Integer> getUserReviewCount(@PathVariable("userId") int userId) {
        try {
            int count = reviewService.getUserReviewCount(userId);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
        }
    }

    @GetMapping("/user/{userId}/film/{filmId}")
    public ResponseEntity<Review> getUserFilmReview(
            @PathVariable("userId") int userId,
            @PathVariable("filmId") int filmId) {
        return reviewService.getReviewByUserAndFilm(userId, filmId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // update
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

    // delete
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