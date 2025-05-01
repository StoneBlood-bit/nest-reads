package nest.exception;

public class InsufficientTokensException extends RuntimeException {
    public InsufficientTokensException(String message) {
        super(message);
    }
}
