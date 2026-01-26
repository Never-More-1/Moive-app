package movieApp.exception;

public class FilmAlreadyInFavoritesException extends RuntimeException {
    public FilmAlreadyInFavoritesException(String name) {
        super("Фильм  \"" + name + "\" уже в любимых");
    }
}
