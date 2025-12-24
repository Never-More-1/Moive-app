package movieApp.model;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Data
@Component
public class User {
    private int id;
    private String username;
    private int age;
    private String role;
    private String email;
    private LocalDateTime created;
}
