package eventpass.model;

public enum StatusIngresso {
    VALIDO("Válido", "● VÁLIDO"),
    UTILIZADO("Utilizado", "✓ USADO"),
    CANCELADO("Cancelado", "✖ CANCELADO");

    private final String descricao;
    private final String badge;

    StatusIngresso(String descricao, String badge) {
        this.descricao = descricao;
        this.badge = badge;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getBadge() {
        return badge;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
