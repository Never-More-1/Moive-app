package movieApp.service;

import jakarta.transaction.Transactional;
import movieApp.exception.*;
import movieApp.model.*;
import movieApp.model.dto.filmDto.FilmUpdateDto;
import movieApp.model.dto.reviewDto.ReviewCreateDto;
import movieApp.model.dto.reviewDto.ReviewUpdateDto;
import movieApp.model.dto.userDto.UserUpdateDto;
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
            throw new SecurityException("Пользователь не аутентифицирован");
        }
        String currentUsername = authentication.getName();
        Optional<Security> currentUserSecurity = securityRepository.getByUsername(currentUsername);
        if (currentUserSecurity.isEmpty()) {
            throw new SecurityException("Пользователь не аутентифицирован");
        }
        Security currentSecurity = currentUserSecurity.get();
        User currentUser = currentSecurity.getUser();
        if (currentUser == null) {
            throw new UserNotFoundException(currentUsername);
        }
        Film film = filmRepository.findById(reviewDto.getFilmId())
                .orElseThrow(() -> new FilmNotFoundException("Фильм не найден с ID: " + reviewDto.getFilmId()));
        if (reviewRepository.existsByUserIdAndFilmId(currentUser.getId(), reviewDto.getFilmId())) {
            throw new ReviewAlreadyExistException();
        }
        if (reviewDto.getReviewText() == null || reviewDto.getReviewText().trim().isEmpty()) {
            throw new IllegalArgumentException("Текст отзыва не может быть пустым");
        }
        if (reviewDto.getRating() < 1 || reviewDto.getRating() > 10) {
            throw new IllegalArgumentException("Рейтинг должен быть в диапазоне от 1 до 10");
        }
        Review review = new Review();
        review.setUser(currentUser);
        review.setFilm(film);
        review.setText(reviewDto.getReviewText());
        review.setRating(reviewDto.getRating());
        review.setCreatedAt(LocalDateTime.now());
        Review savedReview = reviewRepository.save(review);
        updateFilmAverageRating(reviewDto.getFilmId());

        return savedReview;
    }

    //read
    public List<Review> getReviewsByUsername(String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new UserNotFoundException(username);
        }
        return reviewRepository.findByUserUsername(username);
    }

    public List<Review> getMyReviews() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Пользователь не аутентифицирован");
        }

        String username = authentication.getName();
        if (!userRepository.existsByUsername(username)) {
            throw new UserNotFoundException(username);
        }
        return reviewRepository.findByUserUsername(username);
    }

    public List<Review> getReviewsByFilmTitle(String filmTitle) {
        if (!filmRepository.existsByTitle(filmTitle)) {
            throw new FilmNotFoundException("Фильм с названием '" + filmTitle + "' не найден");
        }
        return reviewRepository.findByFilmTitle(filmTitle);
    }

    public int getUserReviewCount(String username) {
        return reviewRepository.countByUserUsername(username);
    }

    public Double getAverageRatingByFilmId(int filmId) {
        if (!filmRepository.existsById(filmId)) {
            throw new FilmNotFoundException("Фильм с ID " + filmId + " не найден");
        }
        Double average = reviewRepository.findAverageRatingByFilmId(filmId);
        return average != null ? Math.round(average * 10.0) / 10.0 : 0.0;
    }

    //update
    public void updateFilmAverageRating(int filmId) {
        Double averageRating = getAverageRatingByFilmId(filmId);
        Optional<Film> film = filmRepository.findById(filmId);
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
            throw new SecurityException("Пользователь не аутентифицирован");
        }
        String currentUsername = authentication.getName();
        Optional<Security> currentUserSecurity = securityRepository.getByUsername(currentUsername);
        if (currentUserSecurity.isEmpty()) {
            throw new SecurityException("Пользователь не найден");
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
                throw new IllegalArgumentException("Текст отзыва не может быть пустым");
            }
            review.setText(reviewUpdateDto.getReviewText());
        }
        Review updatedReview = reviewRepository.save(review);
        updateFilmAverageRating(film.getId());
        return updatedReview;
    }

    public Film updateFilm(int filmId, FilmUpdateDto filmUpdateDto) {
        Film existingFilm = filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmNotFoundException(filmId));
        if (!existingFilm.getTitle().equals(filmUpdateDto.getTitle())) {
            if (filmRepository.existsByTitle(filmUpdateDto.getTitle())) {
                throw new FilmAlreadyExistException(filmUpdateDto.getTitle());
            }
        }
        existingFilm.setTitle(filmUpdateDto.getTitle());
        existingFilm.setReleaseYear(filmUpdateDto.getReleaseYear());
        existingFilm.setDirector(filmUpdateDto.getDirector());
        return filmRepository.save(existingFilm);
    }

    @Transactional
    public boolean deleteReview(String filmTitle) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Пользователь не аутентифицирован");
        }
        String currentUsername = authentication.getName();
        Optional<Security> currentUserSecurity = securityRepository.getByUsername(currentUsername);
        if (currentUserSecurity.isEmpty()) {
            throw new SecurityException("Пользователь не найден");
        }
        Security currentUserSec = currentUserSecurity.get();
        User currentUser = currentUserSec.getUser();

        Film film = filmRepository.findByTitle(filmTitle)
                .orElseThrow(() -> new FilmNotFoundException("Фильм '" + filmTitle + "' не найден"));
        Optional<Review> reviewOptional = reviewRepository.findByUserIdAndFilmId(currentUser.getId(), film.getId());
        if (reviewOptional.isEmpty()) {
            throw new ReviewNotFoundException();
        }

        Review review = reviewOptional.get();

        boolean isAdmin = currentUserSec.getRole() == Role.ADMIN;
        boolean isOwner = review.getUser() != null && review.getUser().getId() == currentUser.getId();

        if (!isAdmin && !isOwner) {
            throw new SecurityException("У вас нет прав для удаления этого отзыва");
        }

        reviewRepository.delete(review);
        updateFilmAverageRating(film.getId());

        return true;
    }
}