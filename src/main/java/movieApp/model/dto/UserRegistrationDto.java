package movieApp.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.stereotype.Component;
import movieApp.annotation.AdultAge;

@Data
@Component
public class UserRegistrationDto {
    @NotBlank
    @Size(min = 3, max = 15)
    private String username;

    @NotBlank
    @AdultAge
    private int age;

    @NotBlank
    @Pattern(regexp = "[A-z][0-9]{6}")
    private String password;
}
