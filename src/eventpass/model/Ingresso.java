package eventpass.model;

import java.util.UUID;

public class Ingresso {

    private final String codigo;
    private final TipoIngresso tipo;
    private final double preco;
    private StatusIngresso status;

    public Ingresso(TipoIngresso tipo, double precoBase) {
        this.codigo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.tipo = tipo;
        this.preco = precoBase * tipo.getMultiplicadorPreco();
        this.status = StatusIngresso.VALIDO;
    }

    public String getCodigo() {
        return codigo;
    }

    public TipoIngresso getTipo() {
        return tipo;
    }

    public double getPreco() {
        return preco;
    }

    public StatusIngresso getStatus() {
        return status;
    }

    public boolean isUsado() {
        return status == StatusIngresso.UTILIZADO;
    }

    public boolean isValido() {
        return status == StatusIngresso.VALIDO;
    }

    public boolean isCancelado() {
        return status == StatusIngresso.CANCELADO;
    }

    public boolean validarEntrada() {
        if (status != StatusIngresso.VALIDO) {
            return false;
        }
        this.status = StatusIngresso.UTILIZADO;
        return true;
    }

    public boolean cancelar() {
        if (status != StatusIngresso.VALIDO) {
            return false;
        }
        this.status = StatusIngresso.CANCELADO;
        return true;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | R$ %.2f | %s",
                codigo, tipo.getDescricao(), preco, status.getBadge());
    }
}
