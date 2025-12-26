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

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    // Основные методы

    public boolean addReview(ReviewCreateDto reviewDto) throws Exception {
        // Проверка пользователя
        Optional<User> user = userRepository.getUserById(reviewDto.getUserId());
        if (user.isEmpty()) throw new Exception("Пользователь не найден");

        // GUEST не может оставлять отзывы
        if ("GUEST".equalsIgnoreCase(user.get().getRole())) {
            throw new Exception("GUEST не может оставлять отзывы");
        }

        // Проверка на дубликат
        if (reviewRepository.checkReviewExists(reviewDto.getUserId(), reviewDto.getFilmId())) {
            throw new Exception("Вы уже оставляли отзыв на этот фильм");
        }

        // Валидация рейтинга
        if (reviewDto.getRating() < 1 || reviewDto.getRating() > 10) {
            throw new Exception("Рейтинг должен быть от 1 до 10");
        }

        // Создание отзыва
        return reviewRepository.addReview(reviewDto);
    }

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

        return !reviewRepository.checkReviewExists(userId, filmId);
    }

    public Double getAverageRatingByFilmId(int filmId) {
        return reviewRepository.getAverageRatingByFilmId(filmId);
    }

    public int getUserReviewCount(int userId) {
        return reviewRepository.getUserReviewCount(userId);
    }
}