package nest.exception;

public class BookNotInShoppingCartException extends RuntimeException {
    public BookNotInShoppingCartException(String message) {
        super(message);
    }
}
