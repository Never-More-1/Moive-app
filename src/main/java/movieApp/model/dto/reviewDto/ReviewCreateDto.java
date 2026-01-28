package movieApp.model.dto.reviewDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewCreateDto {
    @Schema(
            description = "Film title",
            example = "Интерстеллар",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String filmTitle;
    @NotBlank
    private String reviewText;
    @Min(1)
    @Max(10)
    private Integer rating;

    public String getFilmTitle() {
        return filmTitle;
    }

    public void setFilmTitle(String filmTitle) {
        this.filmTitle = filmTitle;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}