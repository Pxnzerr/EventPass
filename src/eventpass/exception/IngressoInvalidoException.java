package eventpass.exception;

public class IngressoInvalidoException extends EventPassException {
    public IngressoInvalidoException(String codigo) {
        super(String.format("Ingresso com código \"%s\" não foi encontrado no sistema.", codigo));
    }

    public IngressoInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
