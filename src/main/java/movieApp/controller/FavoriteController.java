package movieApp.controller;

import movieApp.model.Favorite;
import movieApp.model.dto.favoriteDto.FavoriteResponseDto;
//import movieApp.service.FavoriteService;
import movieApp.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    @GetMapping("/user")
    public ResponseEntity<?> getUserFavorites() {
        try {
            List<Favorite> favorites = favoriteService.getUserFavorites();
            List<FavoriteResponseDto> responseDto = favorites.stream()
                    .map(FavoriteResponseDto::fromFavorite)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countUserFavorites() {
        try {
            int count = favoriteService.countUserFavorites();
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