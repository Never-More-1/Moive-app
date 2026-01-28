package movieApp.service;

import jakarta.transaction.Transactional;
import movieApp.exception.*;
import movieApp.model.*;
import movieApp.model.dto.filmDto.FilmUpdateDto;
import movieApp.model.dto.reviewDto.ReviewCreateDto;
import movieApp.model.dto.reviewDto.ReviewUpdateDto;
import movieApp.repository.FilmRepository;
import movieApp.repository.ReviewRepository;
import movieApp.repository.SecurityRepository;
import movieApp.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final SecurityRepository securityRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         UserRepository userRepository,
                         FilmRepository filmRepository,
                         SecurityRepository securityRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.filmRepository = filmRepository;
        this.securityRepository = securityRepository;
    }

    @Transactional
    public Review addReview(ReviewCreateDto reviewDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("The user is not authenticated");
        }
        String currentUsername = authentication.getName();
        Optional<Security> currentUserSecurity = securityRepository.getByUsername(currentUsername);
        if (currentUserSecurity.isEmpty()) {
            throw new SecurityException("The user is not authenticated");
        }
        Security currentSecurity = currentUserSecurity.get();
        User currentUser = currentSecurity.getUser();
        if (currentUser == null) {
            throw new UserNotFoundException(currentUsername);
        }
        Film film = filmRepository.findByTitle(reviewDto.getFilmTitle())
                .orElseThrow(() -> new FilmNotFoundException(reviewDto.getFilmTitle()));
        if (reviewRepository.existsByUserIdAndFilmTitle(currentUser.getId(), reviewDto.getFilmTitle())) {
            throw new ReviewAlreadyExistException();
        }
        if (reviewDto.getReviewText() == null || reviewDto.getReviewText().trim().isEmpty()) {
            throw new IllegalArgumentException("The review text cannot be empty");
        }
        if (reviewDto.getRating() < 1 || reviewDto.getRating() > 10) {
            throw new IllegalArgumentException("The rating must be in the range from 1 to 10");
        }
        Review review = new Review();
        review.setUser(currentUser);
        review.setFilm(film);
        review.setText(reviewDto.getReviewText());
        review.setRating(reviewDto.getRating());
        review.setCreatedAt(LocalDateTime.now());
        Review savedReview = reviewRepository.save(review);
        updateFilmAverageRating(reviewDto.getFilmTitle());

        return savedReview;
    }

    public List<Review> getReviewsByUsername(String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new UserNotFoundException(username);
        }
        return reviewRepository.findByUserUsername(username);
    }

    public List<Review> getMyReviews() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("The user is not authenticated");
        }

        String username = authentication.getName();
        if (!userRepository.existsByUsername(username)) {
            throw new UserNotFoundException(username);
        }
        return reviewRepository.findByUserUsername(username);
    }

    public List<Review> getReviewsByFilmTitle(String filmTitle) {
        if (!filmRepository.existsByTitle(filmTitle)) {
            throw new FilmNotFoundException(filmTitle);
        }
        return reviewRepository.findByFilmTitle(filmTitle);
    }

    public int getUserReviewCount(String username) {
        return reviewRepository.countByUserUsername(username);
    }

    public Double getAverageRatingByFilmTitle(String filmTitle) {
        if (!filmRepository.existsByTitle(filmTitle)) {
            throw new FilmNotFoundException(filmTitle);
        }
        Double average = reviewRepository.findAverageRatingByFilmTitle(filmTitle);
        return average != null ? Math.round(average * 10.0) / 10.0 : 0.0;
    }

    public void updateFilmAverageRating(String filmTitle) {
        Double averageRating = getAverageRatingByFilmTitle(filmTitle);
        Optional<Film> film = filmRepository.findByTitle(filmTitle);
        if (film.isPresent()) {
            Film filmToUpdate = film.get();
            filmToUpdate.setRating(averageRating);
            filmRepository.save(filmToUpdate);
        }
    }

    @Transactional
    public Review updateReview(String filmTitle, ReviewUpdateDto reviewUpdateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("The user is not authenticated");
        }
        String currentUsername = authentication.getName();
        Optional<Security> currentUserSecurity = securityRepository.getByUsername(currentUsername);
        if (currentUserSecurity.isEmpty()) {
            throw new SecurityException("User nit found");
        }
        Security currentUserSec = currentUserSecurity.get();
        User currentUser = currentUserSec.getUser();
        Film film = filmRepository.findByTitle(filmTitle)
                .orElseThrow(() -> new FilmNotFoundException(filmTitle));

        Review review = reviewRepository.findByUserIdAndFilmId(currentUser.getId(), film.getId())
                .orElseThrow(() -> new ReviewNotFoundException());
        boolean isAdmin = currentUserSec.getRole() == Role.ADMIN;
        boolean isOwner = review.getUser() != null && review.getUser().getId() == currentUser.getId();

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException();
        }

        if (reviewUpdateDto.getReviewText() != null) {
            if (reviewUpdateDto.getReviewText().trim().isEmpty()) {
                throw new IllegalArgumentException("The review text cannot be empty");
            }
            review.setText(reviewUpdateDto.getReviewText());
        }
        Review updatedReview = reviewRepository.save(review);
        updateFilmAverageRating(film.getTitle());
        return updatedReview;
    }

    @Transactional
    public boolean deleteReview(String filmTitle) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("The user is not authenticated");
        }
        String currentUsername = authentication.getName();
        Optional<Security> currentUserSecurity = securityRepository.getByUsername(currentUsername);
        if (currentUserSecurity.isEmpty()) {
            throw new SecurityException("User not found");
        }
        Security currentUserSec = currentUserSecurity.get();
        User currentUser = currentUserSec.getUser();

        Film film = filmRepository.findByTitle(filmTitle)
                .orElseThrow(() -> new FilmNotFoundException(filmTitle));
        Optional<Review> reviewOptional = reviewRepository.findByUserIdAndFilmId(currentUser.getId(), film.getId());
        if (reviewOptional.isEmpty()) {
            throw new ReviewNotFoundException();
        }

        Review review = reviewOptional.get();

        boolean isAdmin = currentUserSec.getRole() == Role.ADMIN;
        boolean isOwner = review.getUser() != null && review.getUser().getId() == currentUser.getId();

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException();
        }

        reviewRepository.delete(review);
        updateFilmAverageRating(film.getTitle());

        return true;
    }
}