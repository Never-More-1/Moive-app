package movieApp.exception;

public class FavoriteNotFoundException extends RuntimeException {
    public FavoriteNotFoundException() {
        super("The movie has not been added to your favorites");
    }
}