package movieApp.exception;

public class UsernameExistException extends Throwable {

    private String username;
    public UsernameExistException(String username) {
        super("Username already exists: " + username);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

//    @Override
//    public String toString() {
//        return "User with this username already exist";
//    }
}
