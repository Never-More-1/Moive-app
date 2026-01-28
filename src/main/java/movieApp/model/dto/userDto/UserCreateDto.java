package movieApp.model.dto.userDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserCreateDto {
    private int id;
    @NotBlank
    private String username;
    @NotBlank
    @Min(6)
    @Max(120)
    private int age;
    private String role;
    @NotBlank
    @Email
    private String email;
    private LocalDateTime created;

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

}