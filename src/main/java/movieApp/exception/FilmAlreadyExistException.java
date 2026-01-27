package movieApp.exception;

public class FilmAlreadyExistException extends RuntimeException {
    public FilmAlreadyExistException(String title) {
        super("A movie with the title \"" + title + "\" already exists");
    }
}
