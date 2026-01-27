package movieApp.model.dto.userDto;

import jakarta.validation.constraints.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserRegistrationDto {
    @NotBlank
    private String username;
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{6,}$",
            message = "The password must contain at least 6 characters: lowercase and uppercase letters, " +
                    "numbers and at least one special character(@$!%*?&)"
    )
    private String password;
    @NotBlank
    @Email(message = "Email must be correct")
    private String email;
    @Min(6)
    @Max(120)
    private int age;
    private LocalDateTime createdAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
