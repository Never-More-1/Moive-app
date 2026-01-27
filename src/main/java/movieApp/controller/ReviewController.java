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
        name = "Review management",
        description = "Movie review operations: creating, reading, updating, deleting"
)
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @Operation(
            summary = "Create a new review",
            description = "Creating a film review. Authorization required"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review successfully created"),
            @ApiResponse(responseCode = "400", description = "Incorrect data"),
            @ApiResponse(responseCode = "401", description = "Authorization required")
    })
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewCreateDto reviewDto) {
        try {
            Review createdReview = reviewService.addReview(reviewDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/myself")
    @Operation(
            summary = "Get my reviews",
            description = "Getting all reviews from the current logged in user"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews found"),
            @ApiResponse(responseCode = "204", description = "No reviews"),
            @ApiResponse(responseCode = "401", description = "Authorization required")
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

    @GetMapping("/{username}")
    @Operation(
            summary = "Get user reviews",
            description = "Get all reviews by username"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews found"),
            @ApiResponse(responseCode = "204", description = "No reviews"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<Review>> getUserReviews(
            @Parameter(description = "Username",
                    example = "dante",
                    required = true)
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

    @GetMapping("/{filmTitle}")
    @Operation(
            summary = "Get movie reviews",
            description = "Get all reviews for a movie title"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews found"),
            @ApiResponse(responseCode = "204", description = "No reviews"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<List<Review>> getFilmReviews(
            @Parameter(description = "Film title", example = "Интерстеллар", required = true)
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

    @GetMapping("/count/{username}")
    @Operation(
            summary = "Get the number of user reviews",
            description = "Getting the total number of user reviews"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Amount found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Integer> getUserReviewCount(
            @Parameter(description = "Username", example = "dante", required = true)
            @PathVariable("username") String username) {
        try {
            int count = reviewService.getUserReviewCount(username);
            return ResponseEntity.ok(count);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{filmTitle}")
    @Operation(
            summary = "Update your movie review",
            description = "Update the current user's review of the specified movie"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review updated"),
            @ApiResponse(responseCode = "404", description = "Review not found"),
            @ApiResponse(responseCode = "401", description = "Authorization required")
    })
    public ResponseEntity<Review> updateReviewByUserAndFilm(
            @Parameter(description = "Film title", example = "Интерстеллар", required = true)
            @PathVariable("filmTitle") String filmTitle,
            @Valid @RequestBody ReviewUpdateDto reviewUpdate) {
        try {
            Review updatedReview = reviewService.updateReview(filmTitle, reviewUpdate);
            return ResponseEntity.ok(updatedReview);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{filmTitle}")
    @Operation(
            summary = "Delete movie review",
            description = "Delete the current user's review of the specified movie"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review deleted"),
            @ApiResponse(responseCode = "404", description = "Review not found"),
            @ApiResponse(responseCode = "401", description = "Authorization required")
    })
    public ResponseEntity<String> deleteReviewByUserAndFilm(
            @Parameter(description = "Film title", example = "Интерстеллар", required = true)
            @PathVariable("filmTitle") String filmTitle) {
        boolean deleted = reviewService.deleteReview(filmTitle);
        return deleted ?
                ResponseEntity.ok("Review deleted") :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found");
    }
}