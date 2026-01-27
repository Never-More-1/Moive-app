package movieApp.model.dto.filmDto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FilmCreateDto {

    @NotBlank(message = "Movie title cannot be empty")
    @Size(min = 1, max = 255, message = "Movie title must be between 1 and 255 characters long")
    private String title;

    @NotNull(message = "The year of release cannot be empty")
    @Min(value = 1888, message = "The year of production cannot be earlier than 1888")
    private Integer releaseYear;

    @NotBlank(message = "Director's name cannot be empty")
    @Size(min = 2, max = 100, message = "The director's name must be between 2 and 100 characters")
    private String director;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

}