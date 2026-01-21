//package movieApp.service;
//
//import movieApp.exception.*;
//import movieApp.model.Film;
//import movieApp.model.Review;
//import movieApp.model.dto.reviewDto.ReviewCreateDto;
//import movieApp.model.dto.reviewDto.ReviewUpdateDto;
//import movieApp.repository.FilmRepository;
//import movieApp.repository.ReviewRepository;
//import movieApp.repository.UserRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class ReviewService {
//    private final ReviewRepository reviewRepository;
//    private final UserRepository userRepository;
//    private final FilmRepository filmRepository;
//
//    public ReviewService(ReviewRepository reviewRepository,
//                         UserRepository userRepository,
//                         FilmRepository filmRepository) {
//        this.reviewRepository = reviewRepository;
//        this.userRepository = userRepository;
//        this.filmRepository = filmRepository;
//    }
//
//    //create
//    public Review addReview(ReviewCreateDto reviewCreateDto) {
//        if (!userRepository.existsById() {
//            throw new UserNotFoundException(reviewCreateDto.getUserId());
//        }
//
//        Film film = filmRepository.findById(reviewCreateDto.getFilmId())
//                .orElseThrow(() -> new FilmNotFoundException(reviewCreateDto.getFilmId()));
//
//        if (reviewRepository.existsByUserIdAndFilmId(reviewCreateDto.getUserId(), reviewCreateDto.getFilmId())) {
//            throw new RuntimeException("Вы уже оставляли отзыв на этот фильм");
//        }
//
//        if (reviewCreateDto.getRating() < 1 || reviewCreateDto.getRating() > 10) {
//            throw new RuntimeException("Рейтинг должен быть от 1 до 10");
//        }
//
//        Review newReview = new Review();
//        newReview.setUser(reviewCreateDto.getUser());
//        newReview.setFilm(reviewCreateDto.getFilm());
//        newReview.setRating(reviewCreateDto.getRating());
//        newReview.setReviewText(reviewCreateDto.getReviewText());
//
//        Review savedReview = reviewRepository.save(newReview);
//        updateFilmAverageRating(reviewCreateDto.getFilmId());
//
//        return savedReview;
//    }
//
//    //read
//    public List<Review> getReviewsByUserId(int userId) {
//        if (!userRepository.existsById(userId)) {
//            throw new UserNotFoundException(userId);
//        }
//        return reviewRepository.findByUserId(userId);
//    }
//
//    public List<Review> getReviewsByFilmId(int filmId) {
//        if (!filmRepository.existsById(filmId)) {
//            throw new FilmNotFoundException(filmId);
//        }
//        return reviewRepository.findByFilmId(filmId);
//    }
//
//    public Optional<Review> getReviewByUserAndFilm(int filmId) {
//        return reviewRepository.findByReviewFilmId(filmId);
//    }
//
//    public int getUserReviewCount(int userId) {
//        if (!userRepository.existsById(userId)) {
//            throw new UserNotFoundException(userId);
//        }
//        return reviewRepository.countByUserId(userId);
//    }
//
//    public Double getAverageRatingByFilmId(int filmId) {
//        if (!filmRepository.existsById(filmId)) {
//            throw new FilmNotFoundException(filmId);
//        }
//        Double average = reviewRepository.findAverageRatingByFilmId(filmId);
//        return average != null ? Math.round(average * 10.0) / 10.0 : 0.0; // Округление до 1 знака
//    }
//
//    //update
//    public Review updateReviewByUserAndFilm(int userId, int filmId, ReviewUpdateDto reviewUpdateDto) {
//        Review existingReview = reviewRepository.findByReviewFilmId(filmId)
//                .orElseThrow(() -> new ReviewNotFoundException());
//        if (reviewUpdateDto.getRating() < 1 || reviewUpdateDto.getRating() > 10) {
//            throw new ValidRatingException();
//        }
//        existingReview.setRating(reviewUpdateDto.getRating());
//        existingReview.setReviewText(reviewUpdateDto.getReviewText());
//        Review updatedReview = reviewRepository.save(existingReview);
//        updateFilmAverageRating(filmId);
//        return updatedReview;
//    }
//
//    public void updateFilmAverageRating(int filmId) {
//        Double averageRating = getAverageRatingByFilmId(filmId);
//        Optional<Film> film = filmRepository.findById(filmId);
//        if (film.isPresent()) {
//            Film filmToUpdate = film.get();
//            filmToUpdate.setRating(averageRating);
//            filmRepository.save(filmToUpdate);
//        }
//    }
//
//    //delete
//    public boolean removeReviewByUserAndFilm(int userId, int filmId) {
//        Optional<Review> review = reviewRepository.findByReviewFilmId(filmId);
//        if (review.isPresent()) {
//            reviewRepository.deleteById(review.get().getId());
//            updateFilmAverageRating(filmId);
//            return true;
//        }
//        return false;
//    }
//}