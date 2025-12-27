package movieApp.service;

import movieApp.model.Review;
import movieApp.model.User;
import movieApp.model.dto.reviewDto.ReviewCreateDto;
import movieApp.model.dto.reviewDto.ReviewUpdateDto;
import movieApp.repository.ReviewRepository;
import movieApp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final TmdbService tmdbApiService; // ← Добавлено

    public ReviewService(ReviewRepository reviewRepository,
                         UserRepository userRepository,
                         TmdbService tmdbApiService) { // ← Добавлено
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.tmdbApiService = tmdbApiService; // ← Добавлено
    }

    // Обновленный метод addReview
    public boolean addReview(ReviewCreateDto reviewDto) throws Exception {
        // 1. Проверка пользователя
        Optional<User> user = userRepository.getUserById(reviewDto.getUserId());
        if (user.isEmpty()) {
            throw new Exception("Пользователь не найден");
        }

        // 2. Проверка роли пользователя
        if ("GUEST".equalsIgnoreCase(user.get().getRole())) {
            throw new Exception("GUEST не может оставлять отзывы");
        }

        // 3. Проверка существования фильма в TMDB
        boolean movieExists = tmdbApiService.movieExists(reviewDto.getFilmId());
        if (!movieExists) {
            throw new Exception("Фильм с ID " + reviewDto.getFilmId() + " не найден в TMDB");
        }

        // 4. Проверка на дубликат
        if (reviewRepository.checkReviewExists(reviewDto.getUserId(), reviewDto.getFilmId())) {
            throw new Exception("Вы уже оставляли отзыв на этот фильм");
        }

        // 5. Валидация рейтинга
        if (reviewDto.getRating() < 1 || reviewDto.getRating() > 10) {
            throw new Exception("Рейтинг должен быть от 1 до 10");
        }

        // 6. Создание отзыва
        return reviewRepository.addReview(reviewDto);
    }

    // Остальные методы без изменений...
    public List<Review> getReviewsByUserId(int userId) {
        return reviewRepository.getReviewsByUserId(userId);
    }

    public List<Review> getReviewsByFilmId(int filmId) {
        return reviewRepository.getReviewsByFilmId(filmId);
    }

    public Optional<Review> getReviewByUserAndFilm(int userId, int filmId) {
        return reviewRepository.getReviewByUserAndFilm(userId, filmId);
    }

    public boolean updateReviewByUserAndFilm(ReviewUpdateDto reviewUpdate, int userId, int filmId) throws Exception {
        if (reviewUpdate.getRating() < 1 || reviewUpdate.getRating() > 10) {
            throw new Exception("Рейтинг должен быть от 1 до 10");
        }
        return reviewRepository.updateReviewByUserAndFilm(reviewUpdate, userId, filmId);
    }

    public boolean removeReviewByUserAndFilm(int userId, int filmId) throws Exception {
        return reviewRepository.removeReviewByUserAndFilm(userId, filmId);
    }

    public boolean canUserCreateReview(int userId, int filmId) throws Exception {
        Optional<User> user = userRepository.getUserById(userId);
        if (user.isEmpty()) return false;

        if ("GUEST".equalsIgnoreCase(user.get().getRole())) return false;

        // Проверяем существование фильма
        if (!tmdbApiService.movieExists(filmId)) return false;

        return !reviewRepository.checkReviewExists(userId, filmId);
    }

    public Double getAverageRatingByFilmId(int filmId) {
        return reviewRepository.getAverageRatingByFilmId(filmId);
    }

    public int getUserReviewCount(int userId) {
        return reviewRepository.getUserReviewCount(userId);
    }
}