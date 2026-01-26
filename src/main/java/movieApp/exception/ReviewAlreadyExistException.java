package movieApp.exception;

public class ReviewAlreadyExistException extends RuntimeException{
    public ReviewAlreadyExistException(){
        super("Отзыв уже существует");
    }
}
