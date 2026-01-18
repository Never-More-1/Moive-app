package movieApp.exception;

public class FavoriteAlreadyExistsException extends RuntimeException {
    public FavoriteAlreadyExistsException() {
        super("Film already in favorites");
    }
}
