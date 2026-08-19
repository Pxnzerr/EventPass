package eventpass.exception;

public class EventPassException extends RuntimeException {
    public EventPassException(String message) {
        super(message);
    }

    public EventPassException(String message, Throwable cause) {
        super(message, cause);
    }
}
