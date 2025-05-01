package nest.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST);
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorMessage)
                .collect(Collectors.toList());
        body.put("errors", errors);
        return new ResponseEntity<>(body, headers, HttpStatus.BAD_REQUEST);
    }

    private String getErrorMessage(ObjectError e) {
        if (e instanceof FieldError) {
            String field = ((FieldError) e).getField();
            String message = e.getDefaultMessage();
            return field + " " + message;
        }
        return e.getDefaultMessage();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(
            EntityNotFoundException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND,
                "Entity Not Found", ex.getMessage());
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<Object> handleRegistrationException(
            RegistrationException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT,
                "Registration Error", ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED,
                "Authentication Error", ex.getMessage());
    }

    @ExceptionHandler(BookAlreadyInFavoritesException.class)
    public ResponseEntity<Object> handleBookAlreadyInFavoritesException(
            BookAlreadyInFavoritesException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT,
                "Book Already in Favorites", ex.getMessage());
    }

    @ExceptionHandler(BookNotInFavoritesException.class)
    public ResponseEntity<Object> handleBookNotInFavoritesException(
            BookNotInFavoritesException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                "Book Not in Favorites", ex.getMessage());
    }

    @ExceptionHandler(InsufficientTokensException.class)
    public ResponseEntity<Object> handleInsufficientTokensException(
            InsufficientTokensException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.PAYMENT_REQUIRED,
                "Insufficient Tokens", ex.getMessage());
    }

    @ExceptionHandler(BookNotInShoppingCartException.class)
    public ResponseEntity<Object> handleBookNotInShoppingCartException(
            BookNotInShoppingCartException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                "Book Not in Shopping Cart", ex.getMessage());
    }

    @ExceptionHandler(InvalidContentTypeException.class)
    public ResponseEntity<Object> handleInvalidContentType(
            InvalidContentTypeException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Invalid Content-Type", ex.getMessage());
    }

    private ResponseEntity<Object> buildErrorResponse(
            HttpStatus status, String error, String message
    ) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}
