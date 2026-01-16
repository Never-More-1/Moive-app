package movieApp.exception;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(int id) {
        super("Review not found with id: " + id);
    }
}
