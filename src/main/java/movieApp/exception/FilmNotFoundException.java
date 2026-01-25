package movieApp.exception;

public class FilmNotFoundException extends RuntimeException{
    public FilmNotFoundException(int id) {
        super("Фильм с id " + id + " не найден");
    }
    public FilmNotFoundException(String title) {
        super("Фильм с названием " + " не найден");
    }
}
