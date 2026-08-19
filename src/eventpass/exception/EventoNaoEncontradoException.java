package eventpass.exception;

public class EventoNaoEncontradoException extends EventPassException {
    public EventoNaoEncontradoException(int id) {
        super(String.format("Evento com ID #%d não foi encontrado.", id));
    }

    public EventoNaoEncontradoException(String message) {
        super(message);
    }
}
