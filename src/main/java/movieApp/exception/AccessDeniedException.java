package movieApp.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super("Access denied, you're not Admin");
    }
}
