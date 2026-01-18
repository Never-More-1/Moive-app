package movieApp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "movie_user")
public class User {

    @Id
    @SequenceGenerator(
            name = "user_generator",
            sequenceName = "movie_user_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(generator = "user_generator")
    private Integer id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public User() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String username, Integer age, String role, String email) {
        this.username = username;
        this.age = age;
        this.role = role;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}