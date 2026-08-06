package eventpass.model;

import java.util.UUID;

public class Ingresso {

    private final String codigo;
    private final TipoIngresso tipo;
    private final double preco;
    private boolean usado;

    public Ingresso(TipoIngresso tipo, double precoBase) {
        this.codigo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.tipo = tipo;
        this.preco = precoBase * tipo.getMultiplicadorPreco();
        this.usado = false;
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

    public boolean isUsado() {
        return usado;
    }

    public boolean validarEntrada() {
        if (usado) {
            return false;
        }
        this.usado = true;
        return true;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | R$ %.2f | %s",
                codigo, tipo.getDescricao(), preco,
                usado ? "✓ USADO" : "● VÁLIDO");
    }
}
