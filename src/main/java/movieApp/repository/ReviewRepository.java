package movieApp.repository;

import movieApp.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByUserId(Integer userId);
    List<Review> findByFilmId(Integer filmId);
    Optional<Review> findByUserIdAndFilmId(Integer userId, Integer filmId);

    // Проверить существование отзыва
    boolean existsByUserIdAndFilmId(Integer userId, Integer filmId);

    // Посчитать отзывы пользователя
    int countByUserId(Integer userId);

    // Найти средний рейтинг фильма
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.filmId = :filmId")
    Double findAverageRatingByFilmId(@Param("filmId") Integer filmId);
}