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
    List<Review> findByUserUsername(@Param("username") String username);

    List<Review> findByFilmTitle(String filmTitle);

    boolean existsByUserIdAndFilmId(int userId, int filmId);

    int countByUserUsername(String username);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.film.id = :filmId")
    Double findAverageRatingByFilmId(int filmId);

    Optional<Review> findByUserIdAndFilmId(int userId, int filmId);
}