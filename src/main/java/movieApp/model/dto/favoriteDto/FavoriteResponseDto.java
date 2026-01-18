package movieApp.model.dto.favoriteDto;
import lombok.Data;
import movieApp.model.dto.filmDto.FilmInfoDto;

import java.time.LocalDateTime;

@Data
public class FavoriteResponseDto {

    private LocalDateTime createdAt;

    private FilmInfoDto film;

    public static FavoriteResponseDto fromFavorite(movieApp.model.Favorite favorite) {
        if (favorite == null) {
            return null;
        }

        FavoriteResponseDto dto = new FavoriteResponseDto();
        dto.setCreatedAt(favorite.getCreatedAt());

        FilmInfoDto filmInfo = new FilmInfoDto();
        if (favorite.getFilm() != null) {
            filmInfo.setId(favorite.getFilm().getId());
            filmInfo.setTitle(favorite.getFilm().getTitle());
            filmInfo.setReleaseYear(favorite.getFilm().getReleaseYear());
            filmInfo.setDirector(favorite.getFilm().getDirector());
            filmInfo.setRating(favorite.getFilm().getRating());
        }

        dto.setFilm(filmInfo);

        return dto;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public FilmInfoDto getFilm() {
        return film;
    }

    public void setFilm(FilmInfoDto film) {
        this.film = film;
    }
}