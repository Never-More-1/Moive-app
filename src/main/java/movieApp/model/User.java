package movieApp.model;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Data
//TODO: пофиксить Lombok!!!
@Component
public class User {
    private int id;
    private String username;
    private int age;
    private String role;
    private String email;
    private LocalDateTime created_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
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

    public LocalDateTime getCreated() {
        return created_at;
    }

    public void setCreated(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}
