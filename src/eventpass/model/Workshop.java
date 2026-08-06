package eventpass.model;

import java.time.LocalDate;

public class Workshop extends Evento {

    private final String instrutor;
    private final int cargaHoraria;

    public Workshop(String nome, LocalDate data, String local, int capacidadeMaxima,
                    double precoBase, String instrutor, int cargaHoraria) {
        super(nome, data, local, capacidadeMaxima, precoBase);
        this.instrutor = instrutor;
        this.cargaHoraria = cargaHoraria;
    }

    @Override
    public String getTipoEvento() {
        return "🔧 WORKSHOP";
    }

    @Override
    public String getDetalhesEspecificos() {
        return String.format("Instrutor: %s | Carga Horária: %dh", instrutor, cargaHoraria);
    }

    public String getInstrutor() {
        return instrutor;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }
}
