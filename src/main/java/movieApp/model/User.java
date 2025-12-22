package movieApp.model;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Data
@Component
public class User {
    private int id;
//    private String firstName;
//    private String secondName;
    private int age;
    private LocalDateTime created;
//    private LocalDateTime updated;
    private String email;
}
