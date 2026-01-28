package movieApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import movieApp.model.Favorite;
import movieApp.model.dto.favoriteDto.FavoriteResponseDto;
import movieApp.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@Tag(
        name = "Favorites",
        description = "Managing user's favorite movies"
)
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{filmId}")
    @Operation(
            summary = "Add movie to favorites",
            description = "Adding a movie to the current user's favorites list"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The movie has been successfully added to your favorites"),
            @ApiResponse(responseCode = "400", description = "The movie is already in your favorites or not found"),
            @ApiResponse(responseCode = "401", description = "Authorization required"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<?> addToFavoritesWithPath(
            @Parameter(
                    description = "Movie ID",
                    example = "9",
                    required = true
            )
            @PathVariable Integer filmId) {
        try {
            Favorite favorite = favoriteService.addToFavorites(filmId);
            FavoriteResponseDto responseDto = FavoriteResponseDto.fromFavorite(favorite);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{username}")
    @Operation(
            summary = "Get user's favorites",
            description = "Get a list of favorite movies of a specified user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorites list received"),
            @ApiResponse(responseCode = "401", description = "Authorization required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })

    public ResponseEntity<?> getUserFavorites(
            @Parameter(
                    description = "Username",
                    example = "dante",
                    required = true
            )
            @PathVariable String username) {
        try {
            List<Favorite> favorite = favoriteService.findUserFavoritesByUsername(username);
            return ResponseEntity.ok(favorite);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/count/{username}")
    @Operation(
            summary = "Number of favorite films",
            description = "Get the number of favorite movies for a specified user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Amount received"),
            @ApiResponse(responseCode = "401", description = "Authorization required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> countUserFavorites(
            @Parameter(
                    description = "Username",
                    example = "dante",
                    required = true
            )
            @PathVariable String username) {
        try {
            int count = favoriteService.countUserFavorites(username);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{filmId}")
    @Operation(
            summary = "Remove movie from favorites",
            description = "Removing a movie from the current user's favorites list"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The film has been removed from favorites"),
            @ApiResponse(responseCode = "400", description = "The film was not found in your favorites"),
            @ApiResponse(responseCode = "401", description = "Authorization required"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })

    public ResponseEntity<?> deleteFromFavorites(
            @Parameter(
                    description = "Movie ID",
                    example = "9",
                    required = true
            )
            @PathVariable Integer filmId) {
        try {
            favoriteService.deleteFromFavorites(filmId);
            return ResponseEntity.ok("The film has been deleted from favorites");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}