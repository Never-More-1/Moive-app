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

    // all user reviews
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getUserReviews(@PathVariable("userId") int userId) {
        List<Review> reviews = reviewService.getReviewsByUserId(userId);
        return reviews.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(reviews);
    }

    // all reviews of film
    @GetMapping("/film/{filmId}")
    public ResponseEntity<List<Review>> getFilmReviews(@PathVariable("filmId") int filmId) {
        List<Review> reviews = reviewService.getReviewsByFilmId(filmId);
        return reviews.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(reviews);
    }

    // Is it possible to create a review?
    @GetMapping("/can-create/user/{userId}/film/{filmId}")
    public ResponseEntity<Boolean> canCreateReview(
            @PathVariable("userId") int userId,
            @PathVariable("filmId") int filmId) {
        try {
            boolean canCreate = reviewService.canUserCreateReview(userId, filmId);
            return ResponseEntity.ok(canCreate);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    // Median rating of film
    @GetMapping("/film/{filmId}/rating")
    public ResponseEntity<Double> getFilmRating(@PathVariable("filmId") int filmId) {
        Double rating = reviewService.getAverageRatingByFilmId(filmId);
        return ResponseEntity.ok(rating);
    }

    //user count of reviews
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Integer> getUserReviewCount(@PathVariable("userId") int userId) {
        int count = reviewService.getUserReviewCount(userId);
        return ResponseEntity.ok(count);
    }

    //Create
    @PostMapping
    public ResponseEntity<String> createReview(@RequestBody ReviewCreateDto reviewDto) {
        try {
            boolean created = reviewService.addReview(reviewDto);
            return created ?
                    ResponseEntity.status(HttpStatus.CREATED).body("Review created") :
                    ResponseEntity.status(HttpStatus.CONFLICT).body("Error");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Read (user review)
    @GetMapping("/user/{userId}/film/{filmId}")
    public ResponseEntity<Review> getUserFilmReview(
            @PathVariable("userId") int userId,
            @PathVariable("filmId") int filmId) {
        return reviewService.getReviewByUserAndFilm(userId, filmId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Update
    @PutMapping("/user/{userId}/film/{filmId}")
    public ResponseEntity<String> updateReview(
            @PathVariable("userId") int userId,
            @PathVariable("filmId") int filmId,
            @RequestBody ReviewUpdateDto reviewUpdate) {
        try {
            boolean updated = reviewService.updateReviewByUserAndFilm(reviewUpdate, userId, filmId);
            return updated ?
                    ResponseEntity.ok("Review is updated") :
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review is not found");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Delete
    @DeleteMapping("/user/{userId}/film/{filmId}")
    public ResponseEntity<String> deleteReview(
            @PathVariable("userId") int userId,
            @PathVariable("filmId") int filmId) {
        try {
            boolean deleted = reviewService.removeReviewByUserAndFilm(userId, filmId);
            return deleted ?
                    ResponseEntity.ok("Delete is done") :
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review is not found");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
