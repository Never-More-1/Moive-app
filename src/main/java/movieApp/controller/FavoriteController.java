package movieApp.controller;

import movieApp.model.Favorite;
import movieApp.model.dto.favoriteDto.FavoriteResponseDto;
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
    @PostMapping("/add/user/{userId}/film/{filmId}")
    public ResponseEntity<?> addToFavoritesWithPath(
            @PathVariable Integer userId,
            @PathVariable Integer filmId) {
        try {
            Favorite favorite = favoriteService.addToFavorites(userId, filmId);
            FavoriteResponseDto responseDto = FavoriteResponseDto.fromFavorite(favorite);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // read
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserFavorites(@PathVariable int userId) {
        try {
            List<Favorite> favorites = favoriteService.getUserFavorites(userId);
            List<FavoriteResponseDto> responseDto = favorites.stream()
                    .map(FavoriteResponseDto::fromFavorite)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<?> countUserFavorites(@PathVariable int userId) {
        try {
            int count = favoriteService.countUserFavorites(userId);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // delete
    @DeleteMapping("/delete/user/{userId}/film/{filmId}")
    public ResponseEntity<?> removeFromFavorites(
            @PathVariable Integer userId,
            @PathVariable Integer filmId) {
        try {
            Favorite deletedFavorite = favoriteService.deleteFromFavorites(userId, filmId);
            // Преобразуем удаленный объект в DTO
            FavoriteResponseDto responseDto = FavoriteResponseDto.fromFavorite(deletedFavorite);
            return ResponseEntity.ok().body(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}