package movieApp.model.dto.reviewDto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewCreateDto {
    private int userId;
    private int filmId;
    private String reviewText;
    private int rating; // от 1 до 10

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        // Проверка диапазона
        if (rating < 1) rating = 1;
        if (rating > 10) rating = 10;
        this.rating = rating;
    }
}
