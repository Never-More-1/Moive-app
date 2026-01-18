package movieApp.controller;

import jakarta.validation.Valid;
import movieApp.exception.FilmNotFoundException;
import movieApp.model.Film;
import movieApp.model.dto.filmDto.FilmCreateDto;
import movieApp.model.dto.filmDto.FilmUpdateDto;
import movieApp.service.FilmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    // create
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Film> addFilm(@Valid @RequestBody FilmCreateDto filmCreateDto,
                                        @RequestParam("adminUserId") int adminUserId) {
        Film createdFilm = filmService.addFilm(filmCreateDto, adminUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    // read
    @GetMapping
    public ResponseEntity<?> getAllFilms() {
        return ResponseEntity.ok(filmService.getAllFilms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable("id") int id) {
        try {
            Film film = filmService.getFilmById(id);
            return ResponseEntity.ok(film);
        } catch (FilmNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // update
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Film> updateFilm(@PathVariable("id") int filmId,
                                           @Valid @RequestBody FilmUpdateDto filmUpdateDto,
                                           @RequestParam("adminUserId") int adminUserId) {
        Film updatedFilm = filmService.updateFilm(filmId, adminUserId, filmUpdateDto);
        return ResponseEntity.ok(updatedFilm);
    }

    // delete
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFilm(@PathVariable("id") int filmId,
                                        @RequestParam("adminUserId") int adminUserId) {
        try {
            Film deletedFilm = filmService.deleteFilm(adminUserId, filmId);
            return ResponseEntity.ok(deletedFilm);
        } catch (FilmNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}