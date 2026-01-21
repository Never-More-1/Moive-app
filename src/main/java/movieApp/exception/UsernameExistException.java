package movieApp.exception;

public class UsernameExistException extends Throwable {

    private final String username;
    public UsernameExistException(String username) {
        super("Пользователь " + username + " уже существует");
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
