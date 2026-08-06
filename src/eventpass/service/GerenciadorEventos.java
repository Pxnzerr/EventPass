package eventpass.service;

import eventpass.model.*;

import java.util.ArrayList;
import java.util.List;

public class GerenciadorEventos {

    private final List<Evento> eventos;

    public GerenciadorEventos() {
        this.eventos = new ArrayList<>();
    }

    public void cadastrarEvento(Evento evento) {
        eventos.add(evento);
    }

    public List<Evento> listarEventos() {
        return List.copyOf(eventos);
    }

    public Evento buscarEventoPorId(int id) {
        return eventos.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Ingresso venderIngresso(int eventoId, TipoIngresso tipo) {
        Evento evento = buscarEventoPorId(eventoId);
        if (evento == null) {
            return null;
        }
        return evento.venderIngresso(tipo);
    }

    public String validarEntrada(String codigoIngresso) {
        for (Evento evento : eventos) {
            Ingresso ingresso = evento.buscarIngresso(codigoIngresso);
            if (ingresso != null) {
                if (ingresso.validarEntrada()) {
                    return String.format(
                            "✅ ENTRADA VALIDADA!\n" +
                            "   Evento: %s\n" +
                            "   Ingresso: %s (%s)\n" +
                            "   Preço pago: R$ %.2f",
                            evento.getNome(), ingresso.getCodigo(),
                            ingresso.getTipo().getDescricao(), ingresso.getPreco());
                } else {
                    return String.format(
                            "⚠️  INGRESSO JÁ UTILIZADO!\n" +
                            "   Código: %s\n" +
                            "   Evento: %s",
                            ingresso.getCodigo(), evento.getNome());
                }
            }
        }
        return "❌ Ingresso com código \"" + codigoIngresso + "\" não encontrado.";
    }

    public String gerarRelatorio(int eventoId) {
        Evento evento = buscarEventoPorId(eventoId);
        if (evento == null) {
            return "Evento não encontrado.";
        }

        StringBuilder sb = new StringBuilder();
        String separador = "═".repeat(55);

        sb.append("\n").append(separador).append("\n");
        sb.append("       RELATÓRIO DO EVENTO\n");
        sb.append(separador).append("\n\n");

        sb.append(String.format("  Tipo:        %s\n", evento.getTipoEvento()));
        sb.append(String.format("  Nome:        %s\n", evento.getNome()));
        sb.append(String.format("  Data:        %s\n", evento.getData().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        sb.append(String.format("  Local:       %s\n", evento.getLocal()));
        sb.append(String.format("  Detalhes:    %s\n", evento.getDetalhesEspecificos()));

        sb.append("\n").append("─".repeat(55)).append("\n");
        sb.append("  📊 ESTATÍSTICAS DE VENDAS\n");
        sb.append("─".repeat(55)).append("\n\n");

        sb.append(String.format("  Capacidade Total:      %d\n", evento.getCapacidadeMaxima()));
        sb.append(String.format("  Ingressos Vendidos:    %d\n", evento.getTotalVendidos()));
        sb.append(String.format("  Ingressos Disponíveis: %d\n", evento.getIngressosDisponiveis()));
        sb.append(String.format("  Entradas Validadas:    %d\n", evento.getIngressosUsados()));
        sb.append(String.format("  Preço Base:            R$ %.2f\n", evento.getPrecoBase()));

        sb.append("\n").append("─".repeat(55)).append("\n");
        sb.append("  💰 RECEITA\n");
        sb.append("─".repeat(55)).append("\n\n");

        long pista = evento.getIngressosVendidos().stream()
                .filter(i -> i.getTipo() == TipoIngresso.PISTA).count();
        long vip = evento.getIngressosVendidos().stream()
                .filter(i -> i.getTipo() == TipoIngresso.VIP).count();
        long meia = evento.getIngressosVendidos().stream()
                .filter(i -> i.getTipo() == TipoIngresso.MEIA_ENTRADA).count();

        double receitaPista = evento.getIngressosVendidos().stream()
                .filter(i -> i.getTipo() == TipoIngresso.PISTA)
                .mapToDouble(Ingresso::getPreco).sum();
        double receitaVip = evento.getIngressosVendidos().stream()
                .filter(i -> i.getTipo() == TipoIngresso.VIP)
                .mapToDouble(Ingresso::getPreco).sum();
        double receitaMeia = evento.getIngressosVendidos().stream()
                .filter(i -> i.getTipo() == TipoIngresso.MEIA_ENTRADA)
                .mapToDouble(Ingresso::getPreco).sum();

        sb.append(String.format("  Pista (%dx):           R$ %.2f\n", pista, receitaPista));
        sb.append(String.format("  VIP (%dx):             R$ %.2f\n", vip, receitaVip));
        sb.append(String.format("  Meia Entrada (%dx):    R$ %.2f\n", meia, receitaMeia));
        sb.append("\n");
        sb.append(String.format("  ★ RECEITA TOTAL:       R$ %.2f\n", evento.getReceitaTotal()));

        sb.append("\n").append(separador).append("\n");

        if (!evento.getIngressosVendidos().isEmpty()) {
            sb.append("\n  📋 INGRESSOS VENDIDOS:\n\n");
            for (Ingresso ing : evento.getIngressosVendidos()) {
                sb.append("    ").append(ing).append("\n");
            }
        }

        return sb.toString();
    }

    public boolean temEventos() {
        return !eventos.isEmpty();
    }
}
