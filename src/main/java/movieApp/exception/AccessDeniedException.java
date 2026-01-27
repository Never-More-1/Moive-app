package movieApp.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("Access denied, required role - ADMIN");
    }
}
