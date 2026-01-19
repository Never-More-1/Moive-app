package movieApp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Entity(name = "movie_user")
@Data
@EqualsAndHashCode(exclude = "security")
@ToString(exclude = "security")
@Component
public class User {

    @Id
    @SequenceGenerator(name = "user_generator", sequenceName = "movie_user_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "user_generator")
    private Integer id;

    @Column(name = "username")
    private String username;
    private Integer age;
    private LocalDateTime created_at;

    @JsonIgnore //не учитывает это поле в JSON
    @OneToOne(optional = false, mappedBy = "user", cascade = CascadeType.ALL)
    private Security security;

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public LocalDateTime getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }
}