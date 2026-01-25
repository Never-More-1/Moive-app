package movieApp.repository;


import movieApp.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilmRepository extends JpaRepository <Film, Integer>{
    boolean existsByTitle(String title);
    Optional<Film> findByTitle(String title);
}
