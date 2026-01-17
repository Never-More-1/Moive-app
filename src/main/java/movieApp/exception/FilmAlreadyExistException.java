package movieApp.exception;

public class FilmAlreadyExistException extends RuntimeException {
    public FilmAlreadyExistException(String title) {
        super("Фильм с названием " + title + " уже существует");
    }
}
