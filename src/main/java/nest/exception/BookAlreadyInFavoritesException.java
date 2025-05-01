package nest.exception;

public class BookAlreadyInFavoritesException extends RuntimeException {
    public BookAlreadyInFavoritesException(String message) {
        super(message);
    }
}
