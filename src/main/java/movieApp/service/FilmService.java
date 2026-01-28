package movieApp.service;

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

    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

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

    public Film deleteFilm(String filmTitle) {
        Film filmToDelete = filmRepository.findByTitle(filmTitle)
                .orElseThrow(() -> new FilmNotFoundException(filmTitle));
        filmRepository.deleteById(filmToDelete.getId());
        return filmToDelete;
    }
}