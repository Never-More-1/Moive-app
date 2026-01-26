package movieApp.model.dto.authDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с JWT токеном")
public class AuthResponse {

    @JsonProperty("jwt")
    @Schema(
            description = "JWT токен",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYxNjIzOTAyMiwiZXhwIjoxNjE2MjQyNjIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
    )
    private String jwt;

    // Конструктор без параметров (обязателен для Jackson)
    public AuthResponse() {
    }

    // Конструктор с параметром
    public AuthResponse(String jwt) {
        this.jwt = jwt;
    }

    // Геттер
    public String getJwt() {
        return jwt;
    }

    // Сеттер
    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    // toString для отладки
    @Override
    public String toString() {
        return "AuthResponse{jwt='" + (jwt != null ? "***" + jwt.length() + " chars***" : "null") + "'}";
    }
}