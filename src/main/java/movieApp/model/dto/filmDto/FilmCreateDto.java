package movieApp.model.dto.filmDto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FilmCreateDto {

    @NotBlank(message = "Название фильма не может быть пустым")
    @Size(min = 1, max = 255, message = "Название фильма должно быть от 1 до 255 символов")
    private String title;

    @NotNull(message = "Год выпуска не может быть пустым")
    @Min(value = 1888, message = "Год выпуска не может быть меньше 1888") // Первый фильм был в 1888
    private Integer releaseYear;

    @NotBlank(message = "Имя режиссера не может быть пустым")
    @Size(min = 2, max = 100, message = "Имя режиссера должно быть от 2 до 100 символов")
    private String director;

    private Integer adminUserId;

    public FilmCreateDto() {}

    public FilmCreateDto(String title, Integer releaseYear, String director) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.director = director;
    }

    public FilmCreateDto(String title, Integer releaseYear, String director, Integer adminUserId) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.director = director;
        this.adminUserId = adminUserId;
    }

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

    public Integer getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(Integer adminUserId) {
        this.adminUserId = adminUserId;
    }
}