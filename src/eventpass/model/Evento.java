package eventpass.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class Evento {

    private static int contadorId = 1;

    private final int id;
    private final String nome;
    private final LocalDate data;
    private final String local;
    private final int capacidadeMaxima;
    private final double precoBase;
    private final List<Ingresso> ingressosVendidos;

    protected Evento(String nome, LocalDate data, String local, int capacidadeMaxima, double precoBase) {
        this.id = contadorId++;
        this.nome = nome;
        this.data = data;
        this.local = local;
        this.capacidadeMaxima = capacidadeMaxima;
        this.precoBase = precoBase;
        this.ingressosVendidos = new ArrayList<>();
    }

    public abstract String getTipoEvento();

    public abstract String getDetalhesEspecificos();

    public Ingresso venderIngresso(TipoIngresso tipo) {
        if (ingressosVendidos.size() >= capacidadeMaxima) {
            return null;
        }
        Ingresso ingresso = new Ingresso(tipo, precoBase);
        ingressosVendidos.add(ingresso);
        return ingresso;
    }

    public Ingresso buscarIngresso(String codigo) {
        return ingressosVendidos.stream()
                .filter(i -> i.getCodigo().equalsIgnoreCase(codigo))
                .findFirst()
                .orElse(null);
    }

    public int getIngressosDisponiveis() {
        return capacidadeMaxima - ingressosVendidos.size();
    }

    public double getReceitaTotal() {
        return ingressosVendidos.stream()
                .mapToDouble(Ingresso::getPreco)
                .sum();
    }

    public int getTotalVendidos() {
        return ingressosVendidos.size();
    }

    public long getIngressosUsados() {
        return ingressosVendidos.stream().filter(Ingresso::isUsado).count();
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getData() {
        return data;
    }

    public String getLocal() {
        return local;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public double getPrecoBase() {
        return precoBase;
    }

    public List<Ingresso> getIngressosVendidos() {
        return List.copyOf(ingressosVendidos);
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format("[#%d] %s | %s | %s | %s | Capacidade: %d | Preço Base: R$ %.2f",
                id, getTipoEvento(), nome, data.format(fmt), local, capacidadeMaxima, precoBase);
    }
}
