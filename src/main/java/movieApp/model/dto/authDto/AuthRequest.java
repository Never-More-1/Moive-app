package movieApp.model.dto.authDto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username; // или login, в зависимости от вашей системы
    private String password;

    // Если используете Lombok, геттеры/сеттеры не нужны
    // Иначе добавьте:
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
}