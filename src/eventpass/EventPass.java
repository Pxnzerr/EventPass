package eventpass;

import eventpass.model.*;
import eventpass.service.GerenciadorEventos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class EventPass {

    private static final Scanner scanner = new Scanner(System.in);
    private static final GerenciadorEventos gerenciador = new GerenciadorEventos();
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║          🎫  EVENT PASS  🎫                  ║");
        System.out.println("║     Sistema de Gestão de Eventos              ║");
        System.out.println("╚═══════════════════════════════════════════════╝");

        boolean executando = true;

        while (executando) {
            exibirMenu();
            int opcao = lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1 -> cadastrarEvento();
                case 2 -> venderIngresso();
                case 3 -> validarEntrada();
                case 4 -> exibirRelatorio();
                case 5 -> listarEventos();
                case 0 -> {
                    System.out.println("\n👋 Obrigado por usar o EventPass! Até logo.\n");
                    executando = false;
                }
                default -> System.out.println("\n⚠️  Opção inválida. Tente novamente.");
            }
        }

        scanner.close();
    }

    private static void exibirMenu() {
        System.out.println("\n┌───────────────────────────────────────────────┐");
        System.out.println("│               MENU PRINCIPAL                  │");
        System.out.println("├───────────────────────────────────────────────┤");
        System.out.println("│  [1] Cadastrar Evento                        │");
        System.out.println("│  [2] Vender Ingresso                         │");
        System.out.println("│  [3] Validar Entrada                         │");
        System.out.println("│  [4] Relatório do Evento                     │");
        System.out.println("│  [5] Listar Eventos                          │");
        System.out.println("│  [0] Sair                                    │");
        System.out.println("└───────────────────────────────────────────────┘");
    }

    private static void cadastrarEvento() {
        System.out.println("\n═══ CADASTRAR NOVO EVENTO ═══\n");
        System.out.println("  Tipo de Evento:");
        System.out.println("  [1] Show");
        System.out.println("  [2] Workshop");
        System.out.println("  [3] Conferência");

        int tipo = lerInteiro("\n  Escolha o tipo: ");

        System.out.print("  Nome do evento: ");
        String nome = scanner.nextLine().trim();

        LocalDate data = lerData("  Data (dd/MM/yyyy): ");

        System.out.print("  Local: ");
        String local = scanner.nextLine().trim();

        int capacidade = lerInteiro("  Capacidade máxima: ");

        double precoBase = lerDouble("  Preço base do ingresso (R$): ");

        Evento evento;

        switch (tipo) {
            case 1 -> {
                System.out.print("  Artista/Banda: ");
                String artista = scanner.nextLine().trim();
                System.out.print("  Gênero Musical: ");
                String genero = scanner.nextLine().trim();
                evento = new Show(nome, data, local, capacidade, precoBase, artista, genero);
            }
            case 2 -> {
                System.out.print("  Instrutor: ");
                String instrutor = scanner.nextLine().trim();
                int carga = lerInteiro("  Carga horária (horas): ");
                evento = new Workshop(nome, data, local, capacidade, precoBase, instrutor, carga);
            }
            case 3 -> {
                System.out.print("  Palestrante: ");
                String palestrante = scanner.nextLine().trim();
                System.out.print("  Tema principal: ");
                String tema = scanner.nextLine().trim();
                evento = new Conferencia(nome, data, local, capacidade, precoBase, palestrante, tema);
            }
            default -> {
                System.out.println("\n  ⚠️  Tipo inválido. Cadastro cancelado.");
                return;
            }
        }

        gerenciador.cadastrarEvento(evento);
        System.out.println("\n  ✅ Evento cadastrado com sucesso!");
        System.out.println("  " + evento);
        System.out.println("  " + evento.getDetalhesEspecificos());
    }

    private static void venderIngresso() {
        System.out.println("\n═══ VENDER INGRESSO ═══\n");

        if (!gerenciador.temEventos()) {
            System.out.println("  Nenhum evento cadastrado. Cadastre um evento primeiro.");
            return;
        }

        listarEventosResumido();

        int eventoId = lerInteiro("\n  ID do evento: ");
        Evento evento = gerenciador.buscarEventoPorId(eventoId);

        if (evento == null) {
            System.out.println("  ❌ Evento não encontrado.");
            return;
        }

        if (evento.getIngressosDisponiveis() <= 0) {
            System.out.println("  ❌ Evento ESGOTADO! Sem ingressos disponíveis.");
            return;
        }

        System.out.println("\n  Evento selecionado: " + evento.getNome());
        System.out.printf("  Ingressos disponíveis: %d/%d\n",
                evento.getIngressosDisponiveis(), evento.getCapacidadeMaxima());
        System.out.printf("  Preço base: R$ %.2f\n\n", evento.getPrecoBase());

        System.out.println("  Tipo de Ingresso:");
        System.out.printf("  [1] Pista       — R$ %.2f\n",
                evento.getPrecoBase() * TipoIngresso.PISTA.getMultiplicadorPreco());
        System.out.printf("  [2] VIP         — R$ %.2f\n",
                evento.getPrecoBase() * TipoIngresso.VIP.getMultiplicadorPreco());
        System.out.printf("  [3] Meia Entrada — R$ %.2f\n",
                evento.getPrecoBase() * TipoIngresso.MEIA_ENTRADA.getMultiplicadorPreco());

        int tipoOpcao = lerInteiro("\n  Escolha o tipo: ");

        TipoIngresso tipoIngresso = switch (tipoOpcao) {
            case 1 -> TipoIngresso.PISTA;
            case 2 -> TipoIngresso.VIP;
            case 3 -> TipoIngresso.MEIA_ENTRADA;
            default -> null;
        };

        if (tipoIngresso == null) {
            System.out.println("  ⚠️  Tipo inválido. Venda cancelada.");
            return;
        }

        Ingresso ingresso = gerenciador.venderIngresso(eventoId, tipoIngresso);

        if (ingresso != null) {
            System.out.println("\n  ✅ INGRESSO VENDIDO COM SUCESSO!");
            System.out.println("  ┌─────────────────────────────────────┐");
            System.out.printf("  │  Código:  %s                  │\n", ingresso.getCodigo());
            System.out.printf("  │  Tipo:    %-26s │\n", ingresso.getTipo().getDescricao());
            System.out.printf("  │  Valor:   R$ %-22.2f │\n", ingresso.getPreco());
            System.out.printf("  │  Evento:  %-26s │\n",
                    evento.getNome().length() > 26 ? evento.getNome().substring(0, 23) + "..." : evento.getNome());
            System.out.println("  └─────────────────────────────────────┘");
            System.out.println("\n  ⚠️  Guarde o código do ingresso para validação na entrada!");
        } else {
            System.out.println("  ❌ Não foi possível vender o ingresso. Evento esgotado.");
        }
    }

    private static void validarEntrada() {
        System.out.println("\n═══ VALIDAR ENTRADA ═══\n");

        if (!gerenciador.temEventos()) {
            System.out.println("  Nenhum evento cadastrado.");
            return;
        }

        System.out.print("  Código do ingresso: ");
        String codigo = scanner.nextLine().trim();

        if (codigo.isEmpty()) {
            System.out.println("  ⚠️  Código não informado.");
            return;
        }

        String resultado = gerenciador.validarEntrada(codigo);
        System.out.println("\n  " + resultado.replace("\n", "\n  "));
    }

    private static void exibirRelatorio() {
        System.out.println("\n═══ RELATÓRIO DO EVENTO ═══\n");

        if (!gerenciador.temEventos()) {
            System.out.println("  Nenhum evento cadastrado.");
            return;
        }

        listarEventosResumido();

        int eventoId = lerInteiro("\n  ID do evento para relatório: ");
        String relatorio = gerenciador.gerarRelatorio(eventoId);
        System.out.println(relatorio);
    }

    private static void listarEventos() {
        System.out.println("\n═══ EVENTOS CADASTRADOS ═══\n");

        List<Evento> eventos = gerenciador.listarEventos();

        if (eventos.isEmpty()) {
            System.out.println("  Nenhum evento cadastrado.");
            return;
        }

        for (Evento e : eventos) {
            System.out.println("  " + e);
            System.out.println("    " + e.getDetalhesEspecificos());
            System.out.printf("    Vendidos: %d | Disponíveis: %d | Receita: R$ %.2f\n\n",
                    e.getTotalVendidos(), e.getIngressosDisponiveis(), e.getReceitaTotal());
        }
    }

    private static void listarEventosResumido() {
        System.out.println("  Eventos disponíveis:");
        for (Evento e : gerenciador.listarEventos()) {
            System.out.printf("    #%d — %s (%s) | Disponíveis: %d\n",
                    e.getId(), e.getNome(), e.getTipoEvento(), e.getIngressosDisponiveis());
        }
    }

    private static int lerInteiro(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("  ⚠️  Informe um número inteiro válido.");
            }
        }
    }

    private static double lerDouble(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String input = scanner.nextLine().trim().replace(",", ".");
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("  ⚠️  Informe um valor numérico válido.");
            }
        }
    }

    private static LocalDate lerData(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input, FMT_DATA);
            } catch (DateTimeParseException e) {
                System.out.println("  ⚠️  Data inválida. Use o formato dd/MM/yyyy.");
            }
        }
    }
}
