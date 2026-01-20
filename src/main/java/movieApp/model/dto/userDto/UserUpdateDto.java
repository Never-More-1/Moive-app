package movieApp.model.dto.userDto;

import jakarta.validation.constraints.*;
import lombok.Data;

//TODO: пофиксить Lombok!!!
@Data
public class UserUpdateDto {
    private int id;

    @Size(min = 3, max = 15, message = "Username должен быть минимум 3 символа, а максимум 15")
    private String username;

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Min(value = 1)
    @Max(value = 120)
    private int age;

//    @Pattern(regexp = "USER", message = "Роль должна быть USER или ADMIN")
//    private String role;

    @Email(message = "Email должен быть корректным")
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

//    public String getRole() {
//        return role;
//    }
//
//    public void setRole(String role) {
//        this.role = role;
//    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
