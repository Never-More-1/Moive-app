package movieApp.exception;

public class FilmNotFoundException extends RuntimeException{
    public FilmNotFoundException(int id) {
        super("Movie with id \"" + id + "\" not found");
    }
    public FilmNotFoundException(String title) {
        super(
                "A movie with the title \"" + title + "\" was not found");
    }
}
