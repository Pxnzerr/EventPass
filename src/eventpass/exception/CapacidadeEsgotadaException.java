package eventpass.exception;

public class CapacidadeEsgotadaException extends EventPassException {
    public CapacidadeEsgotadaException(String nomeEvento, int capacidade) {
        super(String.format("O evento \"%s\" atingiu a capacidade máxima de %d ingressos.", nomeEvento, capacidade));
    }

    public CapacidadeEsgotadaException(String message) {
        super(message);
    }
}
