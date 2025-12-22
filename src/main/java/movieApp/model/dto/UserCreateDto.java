package movieApp.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserCreateDto {
    private int id;
    private String username;
    private String email;
    private String password;
//    private String roles;
//    private LocalDateTime created;
}