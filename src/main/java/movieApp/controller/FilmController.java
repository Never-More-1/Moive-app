package movieApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.List;

@RestController
@RequestMapping("/films")
@Tag(
        name = "Movie management",
        description = "Movie operations: viewing, adding, updating, deleting"
)
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/create")
    @Operation(
            summary = "Add new movie",
            description = "Adding a movie to the site (requires ADMIN rights)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movie added successfully"),
            @ApiResponse(responseCode = "400", description = "Incorrect movie data"),
            @ApiResponse(responseCode = "401", description = "Authorization required"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights (requires ADMIN role)")
    })
    public ResponseEntity<Film> addFilm(@Valid @RequestBody FilmCreateDto filmCreateDto) {
        Film createdFilm = filmService.addFilm(filmCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    @GetMapping
    @Operation(
            summary = "Get all movies",
            description = "Getting a list of all movies on a site"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie list received"),
            @ApiResponse(responseCode = "204", description = "No movies found")
    })
    public ResponseEntity<List<Film>> getAllFilms() {
        List<Film> films = filmService.getAllFilms();
        if (films.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(films);
    }

    @GetMapping("/{title}")
    @Operation(
            summary = "Find a movie by title",
            description = "Search for a movie by exact title"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Film found"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<Film> getFilm(
            @Parameter(
                    description = "Movie title",
                    example = "Интерстеллар",
                    required = true
            )
            @PathVariable("title") String title) {
        try {
            Film film = filmService.getFilmByTitle(title);
            return ResponseEntity.ok(film);
        } catch (FilmNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update movie information",
            description = "Update movie data: title, year of release, director, rating (requires ADMIN rights)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The film has been successfully updated"),
            @ApiResponse(responseCode = "400", description = "Incorrect data"),
            @ApiResponse(responseCode = "401", description = "Authorization required"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights (requires ADMIN role)"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<Film> updateFilm(
            @Parameter(
                    description = "Movie ID",
                    example = "9",
                    required = true
            )
            @PathVariable("id") int filmId,
            @Valid @RequestBody FilmUpdateDto filmUpdateDto) {
        Film updatedFilm = filmService.updateFilm(filmId, filmUpdateDto);
        return ResponseEntity.ok(updatedFilm);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete movie",
            description = "Removing a movie from the site (requires ADMIN rights)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The film was successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Authorization required"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights (requires ADMIN role)"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<?> deleteFilm(
            @Parameter(
                    description = "Movie ID",
                    example = "9",
                    required = true
            )
            @PathVariable("id") int filmId) {
        try {
            Film deletedFilm = filmService.deleteFilm(filmId);
            return ResponseEntity.ok(deletedFilm);
        } catch (FilmNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}