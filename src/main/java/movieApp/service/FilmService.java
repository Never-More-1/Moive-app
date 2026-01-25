package movieApp.service;

import movieApp.exception.AccessDeniedException;
import movieApp.exception.FilmAlreadyExistException;
import movieApp.exception.FilmNotFoundException;
import movieApp.model.Film;
import movieApp.model.dto.filmDto.FilmCreateDto;
import movieApp.model.dto.filmDto.FilmUpdateDto;
import movieApp.repository.FilmRepository;
import movieApp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FilmService {
    private final FilmRepository filmRepository;

    public FilmService(FilmRepository filmRepository, UserRepository userRepository) {
        this.filmRepository = filmRepository;
    }

    //create
    public Film addFilm(FilmCreateDto filmCreateDto) {

        if (filmRepository.existsByTitle(filmCreateDto.getTitle())) {
            throw new FilmAlreadyExistException(filmCreateDto.getTitle());
        }

        Film newFilm = new Film();
        newFilm.setTitle(filmCreateDto.getTitle());
        newFilm.setReleaseYear(filmCreateDto.getReleaseYear());
        newFilm.setDirector(filmCreateDto.getDirector());
        newFilm.setRating(0.0);

        return filmRepository.save(newFilm);
    }

    //read
    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    public Film getFilmByTitle(String title) {
        Optional <Film> film = filmRepository.findByTitle(title);
        if (film.isPresent()) {
            return filmRepository.findByTitle(title).get();
        }else{
            throw new FilmNotFoundException(title);
        }
    }
    //update
    public Film updateFilm(int filmId, FilmUpdateDto filmUpdateDto) {
        Film existingFilm = filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmNotFoundException(filmId));
        if (!existingFilm.getTitle().equals(filmUpdateDto.getTitle())) {
            if (filmRepository.existsByTitle(filmUpdateDto.getTitle())) {
                throw new FilmAlreadyExistException(filmUpdateDto.getTitle());
            }
        }
        existingFilm.setTitle(filmUpdateDto.getTitle());
        existingFilm.setReleaseYear(filmUpdateDto.getReleaseYear());
        existingFilm.setDirector(filmUpdateDto.getDirector());
        return filmRepository.save(existingFilm);
    }

    //delete
    public Film deleteFilm(int filmId) {
        Film filmToDelete = filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmNotFoundException(filmId));
        filmRepository.deleteById(filmId);
        return filmToDelete;
    }
}