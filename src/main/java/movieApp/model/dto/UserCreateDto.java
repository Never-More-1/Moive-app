package movieApp.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserCreateDto {
    private int id;
    private String username;
    private int age;
    private String role;
    private String email;
    private LocalDateTime created;
//    private String password;
}