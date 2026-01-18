package movieApp.repository;

import movieApp.model.Favorite;
import movieApp.model.Film;
import movieApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    List<Favorite> findByUser(User user);
    Optional<Favorite> findByUserAndFilm(User user, Film film);
    boolean existsByUserAndFilm(User user, Film film);
    int countByUserId(Integer userId);
}
