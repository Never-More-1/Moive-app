package movieApp.model.dto.reviewDto;

import lombok.Data;

@Data
public class ReviewCreateDto {
    private int filmTitle;
    private String reviewText;
    private double rating; // от 1 до 10

    public int getFilmTitle() {
        return filmTitle;
    }

    public void setFilmTitle(int filmTitle) {
        this.filmTitle = filmTitle;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        if (rating < 1) rating = 1;
        if (rating > 10) rating = 10;
        this.rating = rating;
    }
}
