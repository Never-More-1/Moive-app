package movieApp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "film_id", nullable = false)
    private Film film; // Связь с фильмом

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Связь с пользователем

    @Column(nullable = false, length = 2000)
    private String text;

    @Column(nullable = false)
    private Double rating; // например, от 1 до 5

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Конструкторы
    public Review() {
        this.createdAt = LocalDateTime.now();
    }

    public Review(Film film, User user, String text, Double rating) {
        this.film = film;
        this.user = user;
        this.text = text;
        this.rating = rating;
        this.createdAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры
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
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}