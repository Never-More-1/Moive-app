package movieApp.model.dto.authDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
//@AllArgsConstructor - нужно пофиксить Lombok
public class AuthResponse {
    private String jwt;


    public AuthResponse() {}

    public AuthResponse(String jwt) {
        this.jwt = jwt;
    }
}
