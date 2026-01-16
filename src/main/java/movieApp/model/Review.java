package movieApp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review")
public class Review {

    @Id
    @SequenceGenerator(
            name = "review_generator",
            sequenceName = "review_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(generator = "review_generator")
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "film_id", nullable = false)
    private Integer filmId;

    @Column(name = "review_text", nullable = false, columnDefinition = "TEXT")
    private String reviewText;

    @Column(name = "rating", nullable = false)
    private Double rating; // от 1 до 10

    public Review(){
    }

    public Review(Integer userId, Integer filmId, String reviewText, Integer rating) {
        this.userId = userId;
        this.filmId = filmId;
        this.reviewText = reviewText;
        setRating(rating);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFilmId() {
        return filmId;
    }

    public void setFilmId(Integer filmId) {
        this.filmId = filmId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        if (rating < 1) rating = 1;
        if (rating > 10) rating = 10;
        this.rating = rating;
    }
}