package movieApp.repository;

import movieApp.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByUserId(int userId);
    List<Review> findByFilmId(int filmId);
    Optional<Review> findByUserIdAndFilmId(int userId, int filmId);
    boolean existsByUserIdAndFilmId(int userId, int filmId);
    int countByUserId(int userId);
    Double findAverageRatingByFilmId(int filmId);
}