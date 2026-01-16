package movieApp.exception;

public class FilmNotFoundException extends RuntimeException{
    public FilmNotFoundException(int id) {
        super("Фильм с id " + id + " не найден");
    }
}
