//package movieApp.service;
//
//import movieApp.exception.FavoriteNotFoundException;
//import movieApp.exception.FilmAlreadyExistException;
//import movieApp.exception.FilmNotFoundException;
//import movieApp.exception.UserNotFoundException;
//import movieApp.model.Favorite;
//import movieApp.model.Film;
//import movieApp.model.User;
//import movieApp.repository.FavoriteRepository;
//import movieApp.repository.FilmRepository;
//import movieApp.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class FavoriteService {
//
//    private final FavoriteRepository favoriteRepository;
//    private final UserRepository userRepository;
//    private final FilmRepository filmRepository;
//
//    @Autowired
//    public FavoriteService(FavoriteRepository favoriteRepository,
//                           UserRepository userRepository,
//                           FilmRepository filmRepository) {
//        this.favoriteRepository = favoriteRepository;
//        this.userRepository = userRepository;
//        this.filmRepository = filmRepository;
//    }
//
//    // create
//    public Favorite addToFavorites(Integer userId, Integer filmId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException(userId));
//
//        Film film = filmRepository.findById(filmId)
//                .orElseThrow(() -> new FilmNotFoundException(filmId));
//
//        if (favoriteRepository.existsByUserAndFilm(user, film)) {
//            throw new FilmAlreadyExistException(film.getTitle());
//        }
//
//        Favorite favorite = new Favorite(user, film);
//        return favoriteRepository.save(favorite);
//    }
//
//    //read
//    public List<Favorite> getUserFavorites(Integer userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException(userId));
//
//        return favoriteRepository.findByUser(user);
//    }
//
//    public int countUserFavorites(Integer userId) {
//        if (!userRepository.existsById(userId)) {
//            throw new UserNotFoundException(userId);
//        }
//        return favoriteRepository.countByUserId(userId);
//    }
//
//    //delete
//    @Transactional
//    public Favorite deleteFromFavorites(Integer userId, Integer filmId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException(userId));
//
//        Film film = filmRepository.findById(filmId)
//                .orElseThrow(() -> new FilmNotFoundException(filmId));
//
//        Optional<Favorite> favorite = favoriteRepository.findByUserAndFilm(user, film);
//
//        if (favorite.isPresent()) {
//            favoriteRepository.delete(favorite.get());
//        } else {
//            throw new FavoriteNotFoundException();
//        }
//        return favorite.get();
//    }
//}
