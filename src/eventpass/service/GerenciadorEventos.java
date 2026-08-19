package eventpass.service;

import eventpass.model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public List<Evento> buscarPorNome(String termo) {
        if (termo == null || termo.isBlank()) {
            return listarEventos();
        }
        String termoLower = termo.toLowerCase().trim();
        return eventos.stream()
                .filter(e -> e.getNome().toLowerCase().contains(termoLower))
                .toList();
    }

    public List<Evento> buscarPorTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return listarEventos();
        }
        String tipoLower = tipo.toLowerCase().trim();
        return eventos.stream()
                .filter(e -> e.getTipoEvento().toLowerCase().contains(tipoLower))
                .toList();
    }

    public List<Evento> buscarPorFaixaPreco(double precoMin, double precoMax) {
        return eventos.stream()
                .filter(e -> e.getPrecoBase() >= precoMin && e.getPrecoBase() <= precoMax)
                .toList();
    }

    public List<Evento> buscarApenasComVagas() {
        return eventos.stream()
                .filter(e -> e.getIngressosDisponiveis() > 0)
                .toList();
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
                if (ingresso.isCancelado()) {
                    return String.format(
                            "❌ ENTRADA RECUSADA: INGRESSO CANCELADO!\n" +
                            "   Código: %s\n" +
                            "   Evento: %s",
                            ingresso.getCodigo(), evento.getNome());
                }
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

    public String cancelarIngresso(String codigoIngresso) {
        for (Evento evento : eventos) {
            Ingresso ingresso = evento.buscarIngresso(codigoIngresso);
            if (ingresso != null) {
                if (ingresso.isUsado()) {
                    return String.format(
                            "❌ NÃO É POSSÍVEL CANCELAR: Ingresso já foi utilizado!\n" +
                            "   Código: %s\n" +
                            "   Evento: %s",
                            ingresso.getCodigo(), evento.getNome());
                }
                if (ingresso.isCancelado()) {
                    return String.format(
                            "⚠️  INGRESSO JÁ ESTÁ CANCELADO!\n" +
                            "   Código: %s\n" +
                            "   Evento: %s",
                            ingresso.getCodigo(), evento.getNome());
                }
                if (ingresso.cancelar()) {
                    return String.format(
                            "✅ INGRESSO CANCELADO E ESTORNADO COM SUCESSO!\n" +
                            "   Código: %s (%s)\n" +
                            "   Evento: %s\n" +
                            "   Valor estornado: R$ %.2f\n" +
                            "   Vaga liberada no evento!",
                            ingresso.getCodigo(), ingresso.getTipo().getDescricao(),
                            evento.getNome(), ingresso.getPreco());
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
        sb.append(String.format("  Ingressos Cancelados:  %d\n", evento.getIngressosCancelados()));
        sb.append(String.format("  Preço Base:            R$ %.2f\n", evento.getPrecoBase()));

        sb.append("\n").append("─".repeat(55)).append("\n");
        sb.append("  💰 RECEITA (LÍQUIDA)\n");
        sb.append("─".repeat(55)).append("\n\n");

        long pista = evento.getIngressosVendidos().stream()
                .filter(i -> i.getTipo() == TipoIngresso.PISTA && i.getStatus() != StatusIngresso.CANCELADO).count();
        long vip = evento.getIngressosVendidos().stream()
                .filter(i -> i.getTipo() == TipoIngresso.VIP && i.getStatus() != StatusIngresso.CANCELADO).count();
        long meia = evento.getIngressosVendidos().stream()
                .filter(i -> i.getTipo() == TipoIngresso.MEIA_ENTRADA && i.getStatus() != StatusIngresso.CANCELADO).count();

        double receitaPista = evento.getIngressosVendidos().stream()
                .filter(i -> i.getTipo() == TipoIngresso.PISTA && i.getStatus() != StatusIngresso.CANCELADO)
                .mapToDouble(Ingresso::getPreco).sum();
        double receitaVip = evento.getIngressosVendidos().stream()
                .filter(i -> i.getTipo() == TipoIngresso.VIP && i.getStatus() != StatusIngresso.CANCELADO)
                .mapToDouble(Ingresso::getPreco).sum();
        double receitaMeia = evento.getIngressosVendidos().stream()
                .filter(i -> i.getTipo() == TipoIngresso.MEIA_ENTRADA && i.getStatus() != StatusIngresso.CANCELADO)
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

    public Path exportarRelatorioTxt(int eventoId, String caminho) throws IOException {
        Evento evento = buscarEventoPorId(eventoId);
        if (evento == null) {
            throw new IllegalArgumentException("Evento com ID " + eventoId + " não encontrado.");
        }
        String relatorio = gerarRelatorio(eventoId);
        Path path = Path.of(caminho != null && !caminho.isBlank() ? caminho : "relatorio_evento_" + eventoId + ".txt");
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        Files.writeString(path, relatorio, StandardCharsets.UTF_8);
        return path;
    }

    public Path exportarIngressosCsv(int eventoId, String caminho) throws IOException {
        Evento evento = buscarEventoPorId(eventoId);
        if (evento == null) {
            throw new IllegalArgumentException("Evento com ID " + eventoId + " não encontrado.");
        }
        Path path = Path.of(caminho != null && !caminho.isBlank() ? caminho : "ingressos_evento_" + eventoId + ".csv");
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        StringBuilder csv = new StringBuilder();
        csv.append("Codigo,Tipo,Preco,Status\n");
        for (Ingresso ing : evento.getIngressosVendidos()) {
            csv.append(String.format("%s,%s,%.2f,%s\n",
                    ing.getCodigo(),
                    ing.getTipo().name(),
                    ing.getPreco(),
                    ing.getStatus().name()));
        }
        Files.writeString(path, csv.toString(), StandardCharsets.UTF_8);
        return path;
    }

    public boolean temEventos() {
        return !eventos.isEmpty();
    }
}
