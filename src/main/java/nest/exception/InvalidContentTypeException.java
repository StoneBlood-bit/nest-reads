package nest.exception;

public class InvalidContentTypeException extends RuntimeException {
    public InvalidContentTypeException(String message) {
        super(message);
    }
}
