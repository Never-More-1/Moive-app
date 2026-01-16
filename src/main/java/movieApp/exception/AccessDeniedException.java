package movieApp.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("Доступ запрещен, требуемая роль - Админ");
    }
}
