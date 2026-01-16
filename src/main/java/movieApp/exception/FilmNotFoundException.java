package movieApp.exception;

public class FilmNotFoundException extends RuntimeException{
    public FilmNotFoundException(int id) {
        super("Film not found with id: " + id);
    }
}
