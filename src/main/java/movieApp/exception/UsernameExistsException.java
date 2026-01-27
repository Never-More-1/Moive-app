package movieApp.exception;

public class UsernameExistsException extends Exception {
    public UsernameExistsException() {
        super ("User with this username already exists");
    }
}