package movieApp.exception;

public class ReviewAlreadyExistException extends RuntimeException{
    public ReviewAlreadyExistException(){
        super("Review already exists");
    }
}
