package eventpass.exception;

public class OperacaoInvalidaException extends EventPassException {
    public OperacaoInvalidaException(String message) {
        super(message);
    }

    public OperacaoInvalidaException(String message, Throwable cause) {
        super(message, cause);
    }
}
