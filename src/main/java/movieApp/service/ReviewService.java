package movieApp.service;

import movieApp.exception.ReviewNotFoundException;
import movieApp.model.Film;
import movieApp.model.Review;
import movieApp.model.User;
import movieApp.model.dto.reviewDto.ReviewWithFilmDto;
import movieApp.model.dto.reviewDto.ReviewCreateDto;
import movieApp.model.dto.reviewDto.ReviewUpdateDto;
import movieApp.repository.FilmRepository;
import movieApp.repository.ReviewRepository;
import movieApp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         UserRepository userRepository,
                         FilmRepository filmRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.filmRepository = filmRepository;
    }

    // CRUD операции

    // Create
    public Review addReview(ReviewCreateDto reviewCreateDto) {
        User user = userRepository.findById(reviewCreateDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Проверка роли пользователя
        if ("GUEST".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("GUEST не может оставлять отзывы");
        }

        // Проверяем существование фильма
        Film film = filmRepository.findById(reviewCreateDto.getFilmId())
                .orElseThrow(() -> new RuntimeException("Фильм с ID " + reviewCreateDto.getFilmId() + " не найден"));

        // Проверка на дубликат
        if (reviewRepository.existsByUserIdAndFilmId(reviewCreateDto.getUserId(), reviewCreateDto.getFilmId())) {
            throw new RuntimeException("Вы уже оставляли отзыв на этот фильм");
        }

        // Валидация рейтинга
        if (reviewCreateDto.getRating() < 1 || reviewCreateDto.getRating() > 10) {
            throw new RuntimeException("Рейтинг должен быть от 1 до 10");
        }

        // Создание отзыва
        Review newReview = new Review();
        newReview.setUserId(reviewCreateDto.getUserId());
        newReview.setFilmId(reviewCreateDto.getFilmId());
        newReview.setRating(reviewCreateDto.getRating());
        newReview.setReviewText(reviewCreateDto.getReviewText());

        Review savedReview = reviewRepository.save(newReview);

        // Обновляем средний рейтинг фильма
        updateFilmAverageRating(reviewCreateDto.getFilmId());

        return savedReview;
    }

    // Получение отзыва с информацией о фильме по ID отзыва
    public ReviewWithFilmDto getReviewWithFilmInfoById(int reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        Film film = filmRepository.findById(review.getFilmId()).orElse(null);
        User user = userRepository.findById(review.getUserId()).orElse(null);

        return new ReviewWithFilmDto(review, film, user);
    }

    // Получение всех отзывов пользователя с информацией о фильмах
    public List<ReviewWithFilmDto> getUserReviewsWithFilmInfo(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Пользователь не найден");
        }

        List<Review> reviews = reviewRepository.findByUserId(userId);
        return reviews.stream()
                .map(review -> {
                    Film film = filmRepository.findById(review.getFilmId()).orElse(null);
                    User user = userRepository.findById(userId).orElse(null);
                    return new ReviewWithFilmDto(review, film, user);
                })
                .collect(Collectors.toList());
    }

    // Получение всех отзывов на фильм с информацией о пользователях
    public List<ReviewWithFilmDto> getFilmReviewsWithUserInfo(int filmId) {
        if (!filmRepository.existsById(filmId)) {
            throw new RuntimeException("Фильм не найден");
        }

        List<Review> reviews = reviewRepository.findByFilmId(filmId);
        Film film = filmRepository.findById(filmId).orElse(null);

        return reviews.stream()
                .map(review -> {
                    User user = userRepository.findById(review.getUserId()).orElse(null);
                    return new ReviewWithFilmDto(review, film, user);
                })
                .collect(Collectors.toList());
    }

    // Обычные методы (без информации о фильме)
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Optional<Review> getReviewById(int id) {
        return reviewRepository.findById(id);
    }

    public List<Review> getReviewsByUserId(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Пользователь не найден");
        }
        return reviewRepository.findByUserId(userId);
    }

    public List<Review> getReviewsByFilmId(int filmId) {
        if (!filmRepository.existsById(filmId)) {
            throw new RuntimeException("Фильм не найден");
        }
        return reviewRepository.findByFilmId(filmId);
    }

    public Optional<Review> getReviewByUserAndFilm(int userId, int filmId) {
        return reviewRepository.findByUserIdAndFilmId(userId, filmId);
    }

    // Получение информации о фильме по ID отзыва
    public Film getFilmByReviewId(int reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        return filmRepository.findById(review.getFilmId())
                .orElseThrow(() -> new RuntimeException("Фильм не найден"));
    }

    // Получение названия фильма по ID отзыва
    public String getFilmTitleByReviewId(int reviewId) {
        Film film = getFilmByReviewId(reviewId);
        return film.getTitle();
    }

    public Review updateReview(int id, ReviewUpdateDto reviewUpdateDto) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));

        if (reviewUpdateDto.getRating() < 1 || reviewUpdateDto.getRating() > 10) {
            throw new RuntimeException("Рейтинг должен быть от 1 до 10");
        }

        existingReview.setRating(reviewUpdateDto.getRating());
        existingReview.setReviewText(reviewUpdateDto.getReviewText());

        Review updatedReview = reviewRepository.save(existingReview);

        // Обновляем средний рейтинг фильма
        updateFilmAverageRating(existingReview.getFilmId());

        return updatedReview;
    }

    public Review updateReviewByUserAndFilm(int userId, int filmId, ReviewUpdateDto reviewUpdateDto) {
        Review existingReview = reviewRepository.findByUserIdAndFilmId(userId, filmId)
                .orElseThrow(() -> new RuntimeException("Отзыв не найден"));

        if (reviewUpdateDto.getRating() < 1 || reviewUpdateDto.getRating() > 10) {
            throw new RuntimeException("Рейтинг должен быть от 1 до 10");
        }

        existingReview.setRating(reviewUpdateDto.getRating());
        existingReview.setReviewText(reviewUpdateDto.getReviewText());

        Review updatedReview = reviewRepository.save(existingReview);

        updateFilmAverageRating(filmId);

        return updatedReview;
    }

    public boolean removeReviewById(int id) {
        if (!reviewRepository.existsById(id)) {
            return false;
        }

        Optional<Review> review = reviewRepository.findById(id);
        int filmId = review.map(Review::getFilmId).orElse(0);

        reviewRepository.deleteById(id);

        // Обновляем средний рейтинг фильма
        if (filmId > 0) {
            updateFilmAverageRating(filmId);
        }

        return !reviewRepository.existsById(id);
    }

    public boolean removeReviewByUserAndFilm(int userId, int filmId) {
        Optional<Review> review = reviewRepository.findByUserIdAndFilmId(userId, filmId);
        if (review.isPresent()) {
            reviewRepository.deleteById(review.get().getId());

            // Обновляем средний рейтинг фильма
            updateFilmAverageRating(filmId);

            return true;
        }
        return false;
    }

    // Дополнительные методы

    public boolean canUserCreateReview(int userId, int filmId) {
        // Проверка существования пользователя и его роли
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty() || "GUEST".equalsIgnoreCase(user.get().getRole())) {
            return false;
        }

        // Проверка существования фильма в БД
        if (!filmRepository.existsById(filmId)) {
            return false;
        }

        // Проверка на дубликат отзыва
        return !reviewRepository.existsByUserIdAndFilmId(userId, filmId);
    }

    public Double getAverageRatingByFilmId(int filmId) {
        // Проверяем существование фильма
        if (!filmRepository.existsById(filmId)) {
            throw new RuntimeException("Фильм не найден");
        }

        Double average = reviewRepository.findAverageRatingByFilmId(filmId);
        return average != null ? Math.round(average * 10.0) / 10.0 : 0.0; // Округление до 1 знака
    }

    public int getUserReviewCount(int userId) {
        // Проверяем существование пользователя
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Пользователь не найден");
        }
        return reviewRepository.countByUserId(userId);
    }

    // Обновление среднего рейтинга фильма
    public void updateFilmAverageRating(int filmId) {
        Double averageRating = getAverageRatingByFilmId(filmId);
        Optional<Film> film = filmRepository.findById(filmId);
        if (film.isPresent()) {
            Film filmToUpdate = film.get();
            filmToUpdate.setRating(averageRating);
            filmRepository.save(filmToUpdate);
        }
    }

    // Поиск отзывов по названию фильма
    public List<ReviewWithFilmDto> searchReviewsByFilmTitle(String filmTitle) {
        // Находим фильмы с похожим названием
        List<Film> films = filmRepository.findByTitleContainingIgnoreCase(filmTitle);

        // Собираем все отзывы на эти фильмы
        return films.stream()
                .flatMap(film -> reviewRepository.findByFilmId(film.getId()).stream())
                .map(review -> {
                    Film film = filmRepository.findById(review.getFilmId()).orElse(null);
                    User user = userRepository.findById(review.getUserId()).orElse(null);
                    return new ReviewWithFilmDto(review, film, user);
                })
                .collect(Collectors.toList());
    }
}