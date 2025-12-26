package movieApp.model.dto.reviewDto;

import lombok.Data;

@Data
public class ReviewUpdateDto {
    private String reviewText;
    private int rating; // от 1 до 10

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
        if (rating < 1) rating = 1;
        if (rating > 10) rating = 10;
        this.rating = rating;
    }
}
