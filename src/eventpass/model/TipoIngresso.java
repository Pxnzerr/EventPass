package eventpass.model;

public enum TipoIngresso {

    PISTA("Pista", 1.0),
    VIP("VIP", 2.5),
    MEIA_ENTRADA("Meia Entrada", 0.5);

    private final String descricao;
    private final double multiplicadorPreco;

    TipoIngresso(String descricao, double multiplicadorPreco) {
        this.descricao = descricao;
        this.multiplicadorPreco = multiplicadorPreco;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getMultiplicadorPreco() {
        return multiplicadorPreco;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
