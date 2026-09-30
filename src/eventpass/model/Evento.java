package eventpass.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Evento {

    private static int contadorId = 1;

    private final int id;
    private final String nome;
    private final LocalDate data;
    private final String local;
    private final int capacidadeMaxima;
    private final double precoBase;
    private final List<Ingresso> ingressosVendidos;
    private final Map<String, Ingresso> ingressosPorCodigo;

    private int totalIngressosAtivos = 0;
    private long ingressosUsados = 0;
    private long ingressosCancelados = 0;
    private double receitaTotal = 0.0;

    protected Evento(String nome, LocalDate data, String local, int capacidadeMaxima, double precoBase) {
        this.id = contadorId++;
        this.nome = nome;
        this.data = data;
        this.local = local;
        this.capacidadeMaxima = capacidadeMaxima;
        this.precoBase = precoBase;
        this.ingressosVendidos = new ArrayList<>();
        this.ingressosPorCodigo = new HashMap<>();
    }

    public abstract String getTipoEvento();

    public abstract String getDetalhesEspecificos();

    public Ingresso venderIngresso(TipoIngresso tipo) {
        if (getIngressosDisponiveis() <= 0) {
            return null;
        }
        Ingresso ingresso = new Ingresso(tipo, precoBase);
        ingresso.setOnValidateCallback(() -> ingressosUsados++);
        ingresso.setOnCancelCallback(() -> {
            totalIngressosAtivos--;
            ingressosCancelados++;
            receitaTotal -= ingresso.getPreco();
            if (totalIngressosAtivos == 0) {
                receitaTotal = 0.0;
            }
        });

        ingressosVendidos.add(ingresso);
        ingressosPorCodigo.put(ingresso.getCodigo().toUpperCase(), ingresso);
        totalIngressosAtivos++;
        receitaTotal += ingresso.getPreco();
        return ingresso;
    }

    public Ingresso buscarIngresso(String codigo) {
        if (codigo == null) {
            return null;
        }
        return ingressosPorCodigo.get(codigo.toUpperCase());
    }

    public boolean cancelarIngresso(String codigo) {
        Ingresso ingresso = buscarIngresso(codigo);
        if (ingresso == null) {
            return false;
        }
        return ingresso.cancelar();
    }

    public int getIngressosDisponiveis() {
        return capacidadeMaxima - totalIngressosAtivos;
    }

    public int getTotalIngressosAtivos() {
        return totalIngressosAtivos;
    }

    public double getReceitaTotal() {
        return Math.round(receitaTotal * 100.0) / 100.0;
    }

    public int getTotalVendidos() {
        return totalIngressosAtivos;
    }

    public long getIngressosUsados() {
        return ingressosUsados;
    }

    public long getIngressosCancelados() {
        return ingressosCancelados;
    }

    public double getTaxaOcupacao() {
        if (capacidadeMaxima <= 0) {
            return 0.0;
        }
        return (totalIngressosAtivos / (double) capacidadeMaxima) * 100.0;
    }

    public boolean isEsgotado() {
        return getIngressosDisponiveis() <= 0;
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
        return Collections.unmodifiableList(ingressosVendidos);
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
