package movieApp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorite")
public class Favorite {

    @Id
    @SequenceGenerator(
            name = "favorite_generator",
            sequenceName = "favorite_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(generator = "favorite_generator")
    @JsonIgnore
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Favorite() {
        this.createdAt = LocalDateTime.now();
    }

    public Favorite(User user, Film film) {
        this.user = user;
        this.film = film;
        this.createdAt = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}