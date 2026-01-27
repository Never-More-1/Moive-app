package movieApp.model.dto.userDto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserUpdateDto {
    private int id;

    @Size(min = 3, max = 15, message = "Username must be minimum 3, maximum 15")
    private String username;

    private String password;

    @Min(value = 1)
    @Max(value = 120)
    private int age;

    @Email(message = "Email must be correct")
    private String email;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
