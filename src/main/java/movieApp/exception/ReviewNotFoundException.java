package movieApp.exception;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException() {
        super("Отзыв не найден");
    }
}
