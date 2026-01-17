package movieApp.exception;

public class ValidRatingException extends RuntimeException {
    public ValidRatingException() {
        super("Рейтинг должен быть от 1 до 10");
    }
}
