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
        if (getIngressosDisponiveis() <= 0) {
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

    public boolean cancelarIngresso(String codigo) {
        Ingresso ingresso = buscarIngresso(codigo);
        if (ingresso == null) {
            return false;
        }
        return ingresso.cancelar();
    }

    public int getIngressosDisponiveis() {
        return capacidadeMaxima - getTotalIngressosAtivos();
    }

    public int getTotalIngressosAtivos() {
        return (int) ingressosVendidos.stream()
                .filter(i -> i.getStatus() != StatusIngresso.CANCELADO)
                .count();
    }

    public double getReceitaTotal() {
        return ingressosVendidos.stream()
                .filter(i -> i.getStatus() != StatusIngresso.CANCELADO)
                .mapToDouble(Ingresso::getPreco)
                .sum();
    }

    public int getTotalVendidos() {
        return getTotalIngressosAtivos();
    }

    public long getIngressosUsados() {
        return ingressosVendidos.stream().filter(Ingresso::isUsado).count();
    }

    public long getIngressosCancelados() {
        return ingressosVendidos.stream().filter(Ingresso::isCancelado).count();
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

    public static void resetContadorId() {
        contadorId = 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        return id == evento.id;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format("[#%d] %s | %s | %s | %s | Capacidade: %d | Preço Base: R$ %.2f",
                id, getTipoEvento(), nome, data.format(fmt), local, capacidadeMaxima, precoBase);
    }
}
