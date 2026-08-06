package eventpass.model;

import java.time.LocalDate;

public class Show extends Evento {

    private final String artista;
    private final String generoMusical;

    public Show(String nome, LocalDate data, String local, int capacidadeMaxima,
                double precoBase, String artista, String generoMusical) {
        super(nome, data, local, capacidadeMaxima, precoBase);
        this.artista = artista;
        this.generoMusical = generoMusical;
    }

    @Override
    public String getTipoEvento() {
        return "🎵 SHOW";
    }

    @Override
    public String getDetalhesEspecificos() {
        return String.format("Artista: %s | Gênero: %s", artista, generoMusical);
    }

    public String getArtista() {
        return artista;
    }

    public String getGeneroMusical() {
        return generoMusical;
    }
}
