package movieApp.model.dto.userDto;

import jakarta.validation.constraints.*;
import lombok.Data;

//TODO: пофиксить Lombok!!!
@Data
public class UserUpdateDto {
        @Size(min = 3, max = 15, message = "Username must be between 3 and 15 characters")
        private String username;

        @Min(value = 1)
        @Max(value = 120)
        private int age;

        @Pattern(regexp = "GUEST|USER|ADMIN", message = "Role must be GUEST, USER or ADMIN")
        private String role;

        @Email(message = "Email should be valid")
        private String email;


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

        public String getRole() {
                return role;
        }

        public void setRole(String role) {
                this.role = role;
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(String email) {
                this.email = email;
        }

}
