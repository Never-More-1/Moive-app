package movieApp.controller;

import com.fasterxml.jackson.annotation.JsonView;
import movieApp.model.Favorite;
import movieApp.model.User;
import movieApp.model.dto.favoriteDto.FavoriteResponseDto;
//import movieApp.service.FavoriteService;
import movieApp.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // create
    @PostMapping("/film/{filmId}")
    public ResponseEntity<?> addToFavoritesWithPath(
            @PathVariable Integer filmId) {
        try {
            Favorite favorite = favoriteService.addToFavorites(filmId);
            FavoriteResponseDto responseDto = FavoriteResponseDto.fromFavorite(favorite);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // read
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserFavorites(@PathVariable String username) {
        try {
            List<Favorite> favorite = favoriteService.findUserFavoritesByUsername(username);
            return ResponseEntity.ok(favorite);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/count/{username}")
    public ResponseEntity<?> countUserFavorites(@PathVariable String username) {
        try {
            int count = favoriteService.countUserFavorites(username);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // delete
    @DeleteMapping("/film/{filmId}")
    public ResponseEntity<?> deleteFromFavorites(@PathVariable Integer filmId) {
        try {
            favoriteService.deleteFromFavorites(filmId);
            return ResponseEntity.ok("Фильм удален из избранного");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}