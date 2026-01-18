package movieApp.controller;

import jakarta.validation.Valid;
import movieApp.exception.AccessDeniedException;
import movieApp.exception.FilmNotFoundException;
import movieApp.exception.UserNotFoundException;
import movieApp.model.Film;
import movieApp.model.User;
import movieApp.model.dto.filmDto.FilmCreateDto;
import movieApp.model.dto.filmDto.FilmUpdateDto;
import movieApp.service.FilmService;
import movieApp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private final UserService userService;

    public FilmController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    // create
    @PostMapping("/create/{id}")
    public ResponseEntity<Film> addFilm(@Valid @RequestBody FilmCreateDto filmCreateDto,
                                           @PathVariable("id") int userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (!"ADMIN".equals(user.getRole())) {
            throw new AccessDeniedException();
        }
        Film createdFilm = filmService.addFilm(filmCreateDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    //read
    @GetMapping
    public ResponseEntity<?> getAllFilms() {
        return ResponseEntity.ok(filmService.getAllFilms());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable("id") int id) {
        try {
            Film film = filmService.getFilmById(id);
            return ResponseEntity.ok(film);
        } catch (FilmNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //update
    @PutMapping("/update/user/{userId}/film/{filmId}")
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody FilmUpdateDto filmUpdateDto,
                                           @PathVariable("userId") int userId,
                                           @PathVariable("filmId") int filmId ) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (!"ADMIN".equals(user.getRole())) {
            throw new AccessDeniedException();
        }
        Film updatedFilm = filmService.updateFilm(userId, filmId, filmUpdateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedFilm);
    }

    //delete
    @DeleteMapping("/delete/user/{userId}/film/{filmId}")
    public ResponseEntity<Film> deleteFilm(@PathVariable("userId") int userId,
                                           @PathVariable("filmId") int filmId ) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (!"ADMIN".equals(user.getRole())) {
            throw new AccessDeniedException();
        }
        Film deleteFilm = filmService.deleteFilm(userId, filmId);
        return ResponseEntity.status(HttpStatus.CREATED).body(deleteFilm);
    }
}

