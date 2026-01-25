package movieApp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    @JsonIgnore
    private User user;

    @JoinColumn(name = "review_text", nullable = true)
    private String reviewText;

    @Column(nullable = false)
    private Integer rating;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Review() {
        this.createdAt = LocalDateTime.now();
    }

    public Review(Film film, String text, Integer rating) {
        this.film = film;
        this.reviewText = text;
        this.rating = rating;
        this.createdAt = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return reviewText;
    }

    public void setText(String text) {
        this.reviewText = text;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        if (rating == null) {
            this.rating = null;
        } else if (rating < 1) {
            this.rating = 1;
        } else if (rating > 10) {
            this.rating = 10;
        } else {
            this.rating = rating;
        }
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}