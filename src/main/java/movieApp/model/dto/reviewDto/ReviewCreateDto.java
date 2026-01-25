package movieApp.model.dto.reviewDto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ReviewCreateDto {
    private int filmId;
    private String reviewText;
    @Min(1)
    @Max(10)
    private Integer rating;

    public int getFilmId() {
        return filmId;
    }

    public void setFilmId(int filmId) {
        this.filmId = filmId;
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
        if (rating < 1) rating = 1;
        if (rating > 10) rating = 10;
        this.rating = rating;
    }
}