package movieApp.service;

import movieApp.exception.*;
import movieApp.model.Favorite;
import movieApp.model.Film;
import movieApp.model.User;
import movieApp.repository.FavoriteRepository;
import movieApp.repository.FilmRepository;
import movieApp.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository; // Добавьте этот репозиторий

    public FavoriteService(FavoriteRepository favoriteRepository,
                           FilmRepository filmRepository,
                           UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
    }

    // create
    public Favorite addToFavorites(Integer filmId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmNotFoundException(filmId));

        if (favoriteRepository.existsByUserAndFilm(user, film)) {
            throw new FilmAlreadyInFavoritesException(film.getTitle());
        }

        Favorite favorite = new Favorite(user, film);
        return favoriteRepository.save(favorite);
    }

    // read
    public List<Favorite> findUserFavoritesByUsername(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return favoriteRepository.findByUser(user);
    }

    public int countUserFavorites(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return favoriteRepository.countByUser(user);
    }

     //delete
     @Transactional
    public void deleteFromFavorites(Integer filmId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmNotFoundException(filmId));

        if (!favoriteRepository.existsByUserAndFilm(user, film)) {
            throw new FavoriteNotFoundException();
        }

        favoriteRepository.deleteByUserAndFilm(user, film);
    }
}