package eventpass.model;

import java.time.LocalDate;

public class Conferencia extends Evento {

    private final String palestrante;
    private final String tema;

    public Conferencia(String nome, LocalDate data, String local, int capacidadeMaxima,
                       double precoBase, String palestrante, String tema) {
        super(nome, data, local, capacidadeMaxima, precoBase);
        this.palestrante = palestrante;
        this.tema = tema;
    }

    @Override
    public String getTipoEvento() {
        return "🎤 CONFERÊNCIA";
    }

    @Override
    public String getDetalhesEspecificos() {
        return String.format("Palestrante: %s | Tema: %s", palestrante, tema);
    }

    public String getPalestrante() {
        return palestrante;
    }

    public String getTema() {
        return tema;
    }
}
