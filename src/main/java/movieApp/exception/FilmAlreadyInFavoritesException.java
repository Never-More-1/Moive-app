package movieApp.exception;

public class FilmAlreadyInFavoritesException extends RuntimeException {
    public FilmAlreadyInFavoritesException(String name) {
        super("The film \"" + name + "\" is already in your favorites");
    }
}
