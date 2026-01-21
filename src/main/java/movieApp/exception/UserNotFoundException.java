package movieApp.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super("Пользователь с username " + username + " не найден");
    }
    public UserNotFoundException(int id) {
        super("Пользователь с id " + id + " не найден");
    }
}
