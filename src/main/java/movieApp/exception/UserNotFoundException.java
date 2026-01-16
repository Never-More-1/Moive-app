package movieApp.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(int id) {
        super("Пользователь с id " + id + " не найден");
    }
}
