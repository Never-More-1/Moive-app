package movieApp.controller;

import jakarta.validation.Valid;
import movieApp.exception.UserNotFoundException;
import movieApp.model.Review;
import movieApp.model.dto.reviewDto.ReviewCreateDto;
import movieApp.model.dto.reviewDto.ReviewUpdateDto;
import movieApp.service.ReviewService;
import org.apache.tomcat.util.http.parser.Authorization;
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
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewCreateDto reviewDto) {
        try {
            Review createdReview = reviewService.addReview(reviewDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // read
    @GetMapping("/myreviews")
    public ResponseEntity<List<Review>> getMyReviews() {
        try {
            List<Review> reviews = reviewService.getMyReviews();
            if (reviews.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }else{
                return new ResponseEntity<>(reviews, HttpStatus.OK);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Review>> getUserReviews(@PathVariable("username") String username) {
        try {
            List<Review> reviews = reviewService.getReviewsByUsername(username);
            if (reviews.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }else{
                return new ResponseEntity<>(reviews, HttpStatus.OK);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/film/{filmTitle}")
    public ResponseEntity<List<Review>> getFilmReviews(@PathVariable("filmTitle") String filmTitle) {
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
    public ResponseEntity<Double> getFilmRating(@PathVariable("filmId") int filmId) {
        try {
            Double rating = reviewService.getAverageRatingByFilmId(filmId);
            return ResponseEntity.ok(rating);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0.0);
        }
    }

    @GetMapping("/user/{username}/count")
    public ResponseEntity<Integer> getUserReviewCount(@PathVariable("username") String username) {
        try {
            int count = reviewService.getUserReviewCount(username);
            return ResponseEntity.ok(count);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // update
    @PutMapping("/film/{filmTitle}")
    public ResponseEntity<Review> updateReviewByUserAndFilm(
            @PathVariable("filmTitle") String filmTitle,
            @RequestBody ReviewUpdateDto reviewUpdate) {
        try {
            Review updatedReview = reviewService.updateReview(filmTitle, reviewUpdate);
            return ResponseEntity.ok(updatedReview);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
//
    // delete
    @DeleteMapping("/film/{filmTitle}")
    public ResponseEntity<String> deleteReviewByUserAndFilm(
            @PathVariable("filmTitle") String filmTitle) {
        boolean deleted = reviewService.deleteReview(filmTitle);
        return deleted ?
                ResponseEntity.ok("Отзыв удален") :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Отзыв не найден");
    }
}