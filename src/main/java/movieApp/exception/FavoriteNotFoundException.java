package movieApp.exception;

public class FavoriteNotFoundException extends RuntimeException {
    public FavoriteNotFoundException() {
        super("Фильм не добавлен в любимые");
    }
}