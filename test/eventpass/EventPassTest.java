package eventpass;

import eventpass.exception.*;
import eventpass.model.*;
import eventpass.service.GerenciadorEventos;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class EventPassTest {

    private static int totalTestes = 0;
    private static int testesPassaram = 0;
    private static int testesFalharam = 0;

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║         🧪 SUÍTE DE TESTES DO EVENTPASS               ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝\n");

        long inicio = System.currentTimeMillis();

        executar("Cadastro e Polimorfismo de Eventos", EventPassTest::testCadastroEPolimorfismo);
        executar("Cálculo de Preço por Tipo de Ingresso", EventPassTest::testPrecosEMultiplicadores);
        executar("Limite de Capacidade e Esgotamento", EventPassTest::testCapacidadeELimiteDeVendas);
        executar("Validação de Entrada e Uso Único", EventPassTest::testValidacaoEntrada);
        executar("Cancelamento, Estorno e Liberação de Vaga", EventPassTest::testCancelamentoEEstorno);
        executar("Bloqueio de Cancelamento de Ingresso Já Utilizado", EventPassTest::testNaoPermitirCancelarIngressoJaUsado);
        executar("Bloqueio de Entrada com Ingresso Cancelado", EventPassTest::testNaoPermitirValidarIngressoCancelado);
        executar("Filtros Avançados de Busca com Streams", EventPassTest::testFiltrosDeBusca);
        executar("Exportação de Relatório TXT e Ingressos CSV", EventPassTest::testExportacaoRelatorioTxtECsv);
        executar("Lançamento de Exceções de Domínio", EventPassTest::testExcecoesCustomizadas);
        executar("Recálculo Financeiro e Estatísticas do Relatório", EventPassTest::testRecalculoReceitaRelatorio);
        executar("Equals e HashCode nos Modelos Ingresso e Evento", EventPassTest::testEqualsEHashCodeModelos);
        executar("Dashboard Geral, Receita Consolidada e Taxa de Ocupação", EventPassTest::testDashboardGeralETaxaOcupacao);

        long fim = System.currentTimeMillis();

        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.printf("  Total de Testes: %d | ✅ Passaram: %d | ❌ Falharam: %d\n",
                totalTestes, testesPassaram, testesFalharam);
        System.out.printf("  Tempo de Execução: %d ms\n", (fim - inicio));
        System.out.println("═══════════════════════════════════════════════════════");

        if (testesFalharam > 0) {
            System.exit(1);
        }
    }

    private static void executar(String nome, Runnable teste) {
        totalTestes++;
        try {
            teste.run();
            testesPassaram++;
            System.out.printf("  [PASS] %-52s ✅\n", nome);
        } catch (Throwable e) {
            testesFalharam++;
            System.out.printf("  [FAIL] %-52s ❌ (%s)\n", nome, e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    private static void assertTrue(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError("Falha na asserção: " + mensagem);
        }
    }

    private static void assertFalse(boolean condicao, String mensagem) {
        if (condicao) {
            throw new AssertionError("Falha na asserção: " + mensagem);
        }
    }

    private static void assertEquals(Object esperado, Object atual, String mensagem) {
        if (esperado == null && atual == null) return;
        if (esperado != null && esperado.equals(atual)) return;
        throw new AssertionError(String.format("%s -> Esperado: [%s], Atual: [%s]", mensagem, esperado, atual));
    }

    private static void assertEqualsDouble(double esperado, double atual, double delta, String mensagem) {
        if (Math.abs(esperado - atual) > delta) {
            throw new AssertionError(String.format("%s -> Esperado: [%.2f], Atual: [%.2f]", mensagem, esperado, atual));
        }
    }

    // --- TESTES ---

    private static void testCadastroEPolimorfismo() {
        Evento.resetContadorId();
        GerenciadorEventos g = new GerenciadorEventos();

        Show show = new Show("Rock Fest", LocalDate.of(2026, 10, 15), "Arena Hall", 1000, 100.0, "Iron Maiden", "Heavy Metal");
        Workshop workshop = new Workshop("Java Master", LocalDate.of(2026, 11, 20), "Lab Tech", 30, 200.0, "Dr. Heinz", 8);
        Conferencia conf = new Conferencia("AI Summit", LocalDate.of(2026, 12, 5), "Centro de Conv.", 500, 300.0, "Alan Turing", "Inteligência Artificial");

        g.cadastrarEvento(show);
        g.cadastrarEvento(workshop);
        g.cadastrarEvento(conf);

        assertEquals(3, g.listarEventos().size(), "Deve cadastrar 3 eventos");
        assertTrue(show.getTipoEvento().contains("SHOW"), "Tipo de show deve conter 'SHOW'");
        assertTrue(show.getDetalhesEspecificos().contains("Iron Maiden"), "Detalhes devem conter artista");
        assertTrue(workshop.getTipoEvento().contains("WORKSHOP"), "Tipo de workshop deve conter 'WORKSHOP'");
        assertTrue(workshop.getDetalhesEspecificos().contains("8h"), "Detalhes do workshop devem conter carga horária");
        assertTrue(conf.getTipoEvento().contains("CONFERÊNCIA"), "Tipo de conferência deve conter 'CONFERÊNCIA'");
        assertTrue(conf.getDetalhesEspecificos().contains("Alan Turing"), "Detalhes da conferência");
    }

    private static void testPrecosEMultiplicadores() {
        Evento show = new Show("Festival", LocalDate.now().plusDays(10), "Parque", 100, 100.0, "Artista", "Pop");

        Ingresso pista = show.venderIngresso(TipoIngresso.PISTA);
        Ingresso vip = show.venderIngresso(TipoIngresso.VIP);
        Ingresso meia = show.venderIngresso(TipoIngresso.MEIA_ENTRADA);

        assertEqualsDouble(100.0, pista.getPreco(), 0.001, "Pista deve ser 100% do preço base");
        assertEqualsDouble(250.0, vip.getPreco(), 0.001, "VIP deve ser 250% do preço base (2.5x)");
        assertEqualsDouble(50.0, meia.getPreco(), 0.001, "Meia deve ser 50% do preço base (0.5x)");
    }

    private static void testCapacidadeELimiteDeVendas() {
        Evento show = new Show("Pocket Show", LocalDate.now().plusDays(5), "Pub", 2, 50.0, "Acústico", "Acústico");

        Ingresso i1 = show.venderIngresso(TipoIngresso.PISTA);
        Ingresso i2 = show.venderIngresso(TipoIngresso.VIP);
        Ingresso i3 = show.venderIngresso(TipoIngresso.MEIA_ENTRADA);

        assertTrue(i1 != null, "Primeiro ingresso vendido");
        assertTrue(i2 != null, "Segundo ingresso vendido");
        assertTrue(i3 == null, "Terceiro ingresso deve ser recusado por capacidade");
        assertEquals(0, show.getIngressosDisponiveis(), "Capacidade restante deve ser 0");
    }

    private static void testValidacaoEntrada() {
        GerenciadorEventos g = new GerenciadorEventos();
        Evento show = new Show("Show Entrada", LocalDate.now().plusDays(1), "Estádio", 10, 80.0, "Banda X", "Rock");
        g.cadastrarEvento(show);

        Ingresso ing = g.venderIngresso(show.getId(), TipoIngresso.PISTA);
        assertTrue(ing.isValido(), "Ingresso recém vendido deve ser válido");
        assertEquals(StatusIngresso.VALIDO, ing.getStatus(), "Status inicial deve ser VALIDO");

        String res1 = g.validarEntrada(ing.getCodigo());
        assertTrue(res1.contains("VALIDADA"), "Primeira validação deve ter sucesso");
        assertTrue(ing.isUsado(), "Ingresso deve estar com status utilizado");
        assertEquals(StatusIngresso.UTILIZADO, ing.getStatus(), "Status deve ser UTILIZADO");

        String res2 = g.validarEntrada(ing.getCodigo());
        assertTrue(res2.contains("JÁ UTILIZADO"), "Segunda validação deve avisar que já foi utilizado");
    }

    private static void testCancelamentoEEstorno() {
        GerenciadorEventos g = new GerenciadorEventos();
        Evento show = new Show("Show Cancel", LocalDate.now().plusDays(2), "Clube", 2, 100.0, "DJ Y", "Eletrônica");
        g.cadastrarEvento(show);

        Ingresso ing1 = g.venderIngresso(show.getId(), TipoIngresso.VIP); // R$ 250
        Ingresso ing2 = g.venderIngresso(show.getId(), TipoIngresso.PISTA); // R$ 100

        assertEquals(0, show.getIngressosDisponiveis(), "Todas as vagas vendidas");
        assertEqualsDouble(350.0, show.getReceitaTotal(), 0.001, "Receita inicial: R$ 350");

        // Cancelar ingresso VIP
        String resCancel = g.cancelarIngresso(ing1.getCodigo());
        assertTrue(resCancel.contains("CANCELADO E ESTORNADO"), "Cancelamento deve ter sucesso");
        assertTrue(ing1.isCancelado(), "Ingresso 1 deve estar com status CANCELADO");
        assertEquals(StatusIngresso.CANCELADO, ing1.getStatus(), "Status enum deve ser CANCELADO");

        // Vaga liberada
        assertEquals(1, show.getIngressosDisponiveis(), "Deve ter liberado 1 vaga");
        assertEquals(1, show.getTotalVendidos(), "Total vendidos ativos deve ser 1");
        assertEqualsDouble(100.0, show.getReceitaTotal(), 0.001, "Receita deve recalcular para R$ 100 (excluindo os R$ 250 estornados)");

        // Deve ser possível vender nova vaga liberada
        Ingresso ing3 = g.venderIngresso(show.getId(), TipoIngresso.MEIA_ENTRADA); // R$ 50
        assertTrue(ing3 != null, "Deve permitir comprar a vaga liberada");
        assertEqualsDouble(150.0, show.getReceitaTotal(), 0.001, "Nova receita total deve ser R$ 150");
    }

    private static void testNaoPermitirCancelarIngressoJaUsado() {
        GerenciadorEventos g = new GerenciadorEventos();
        Evento show = new Show("Show Usado", LocalDate.now().plusDays(1), "Arena", 10, 50.0, "Cantor Z", "Samba");
        g.cadastrarEvento(show);

        Ingresso ing = g.venderIngresso(show.getId(), TipoIngresso.PISTA);
        g.validarEntrada(ing.getCodigo());

        String resCancel = g.cancelarIngresso(ing.getCodigo());
        assertTrue(resCancel.contains("NÃO É POSSÍVEL CANCELAR"), "Não deve permitir cancelar ingresso já usado");
        assertTrue(ing.isUsado(), "Status do ingresso deve permanecer UTILIZADO");
    }

    private static void testNaoPermitirValidarIngressoCancelado() {
        GerenciadorEventos g = new GerenciadorEventos();
        Evento show = new Show("Show Canc Val", LocalDate.now().plusDays(1), "Arena", 10, 50.0, "Cantor Z", "Samba");
        g.cadastrarEvento(show);

        Ingresso ing = g.venderIngresso(show.getId(), TipoIngresso.PISTA);
        g.cancelarIngresso(ing.getCodigo());

        String resValid = g.validarEntrada(ing.getCodigo());
        assertTrue(resValid.contains("ENTRADA RECUSADA: INGRESSO CANCELADO"), "Não deve autorizar entrada de ingresso cancelado");
    }

    private static void testFiltrosDeBusca() {
        GerenciadorEventos g = new GerenciadorEventos();
        g.cadastrarEvento(new Show("Lollapalooza Brasil", LocalDate.of(2026, 3, 20), "Autódromo", 5000, 400.0, "Vários", "Variados"));
        g.cadastrarEvento(new Show("Rock in Rio", LocalDate.of(2026, 9, 10), "Cidade do Rock", 10000, 500.0, "Vários", "Rock"));
        g.cadastrarEvento(new Workshop("Workshop de Clean Code", LocalDate.of(2026, 4, 15), "Tech Hub", 25, 120.0, "Uncle Bob", 6));
        g.cadastrarEvento(new Conferencia("JavaOne Brasil", LocalDate.of(2026, 5, 20), "Transamérica", 1500, 350.0, "James Gosling", "Java"));

        // Busca por nome
        List<Evento> buscaRock = g.buscarPorNome("rock");
        assertEquals(1, buscaRock.size(), "Busca por 'rock' deve achar 1 evento");
        assertEquals("Rock in Rio", buscaRock.get(0).getNome(), "Nome retornado");

        // Busca por tipo
        List<Evento> shows = g.buscarPorTipo("show");
        assertEquals(2, shows.size(), "Deve encontrar 2 shows");

        List<Evento> workshops = g.buscarPorTipo("workshop");
        assertEquals(1, workshops.size(), "Deve encontrar 1 workshop");

        // Busca por faixa de preço
        List<Evento> faixa = g.buscarPorFaixaPreco(100.0, 360.0);
        assertEquals(2, faixa.size(), "Deve encontrar 2 eventos entre 100 e 360");

        // Busca por vagas
        Evento eventoLotado = new Show("Show Intimista", LocalDate.now(), "Bar", 1, 50.0, "Voz & Violão", "MPB");
        eventoLotado.venderIngresso(TipoIngresso.PISTA);
        g.cadastrarEvento(eventoLotado);

        List<Evento> comVagas = g.buscarApenasComVagas();
        assertEquals(4, comVagas.size(), "Deve listar apenas os 4 eventos com vagas disponíveis");
    }

    private static void testExportacaoRelatorioTxtECsv() {
        try {
            GerenciadorEventos g = new GerenciadorEventos();
            Evento show = new Show("Export Test Show", LocalDate.now().plusDays(30), "Espaço Livre", 50, 100.0, "Band", "Rock");
            g.cadastrarEvento(show);
            g.venderIngresso(show.getId(), TipoIngresso.PISTA);
            g.venderIngresso(show.getId(), TipoIngresso.VIP);

            Path tempTxt = Files.createTempFile("teste_relatorio", ".txt");
            Path tempCsv = Files.createTempFile("teste_ingressos", ".csv");

            try {
                Path salvoTxt = g.exportarRelatorioTxt(show.getId(), tempTxt.toString());
                assertTrue(Files.exists(salvoTxt), "Arquivo TXT deve existir");
                String conteudoTxt = Files.readString(salvoTxt);
                assertTrue(conteudoTxt.contains("RELATÓRIO DO EVENTO"), "Conteúdo TXT deve ter cabeçalho");
                assertTrue(conteudoTxt.contains("Export Test Show"), "Conteúdo TXT deve ter nome do evento");

                Path salvoCsv = g.exportarIngressosCsv(show.getId(), tempCsv.toString());
                assertTrue(Files.exists(salvoCsv), "Arquivo CSV deve existir");
                String conteudoCsv = Files.readString(salvoCsv);
                assertTrue(conteudoCsv.startsWith("Codigo,Tipo,Preco,Status"), "CSV deve ter cabeçalho padronizado");
                assertTrue(conteudoCsv.contains("PISTA"), "CSV deve conter linha do ingresso PISTA");
                assertTrue(conteudoCsv.contains("VIP"), "CSV deve conter linha do ingresso VIP");
            } finally {
                Files.deleteIfExists(tempTxt);
                Files.deleteIfExists(tempCsv);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao testar exportação de arquivos", e);
        }
    }

    private static void testExcecoesCustomizadas() {
        GerenciadorEventos g = new GerenciadorEventos();
        Evento show = new Show("Show Exceptions", LocalDate.now().plusDays(1), "Teatro", 1, 80.0, "Orquestra", "Clássica");
        g.cadastrarEvento(show);

        // Evento não encontrado
        boolean eventoNaoEncontradoLancado = false;
        try {
            g.buscarEventoPorIdOuFalhar(9999);
        } catch (EventoNaoEncontradoException e) {
            eventoNaoEncontradoLancado = true;
        }
        assertTrue(eventoNaoEncontradoLancado, "Deve lançar EventoNaoEncontradoException para ID inexistente");

        // Venda esgotada
        g.venderIngressoComValidacao(show.getId(), TipoIngresso.PISTA);
        boolean capacidadeEsgotadaLancada = false;
        try {
            g.venderIngressoComValidacao(show.getId(), TipoIngresso.VIP);
        } catch (CapacidadeEsgotadaException e) {
            capacidadeEsgotadaLancada = true;
        }
        assertTrue(capacidadeEsgotadaLancada, "Deve lançar CapacidadeEsgotadaException quando esgotado");

        // Ingresso inválido
        boolean ingressoInvalidoLancado = false;
        try {
            g.validarEntradaComValidacao("CODIGO_INEXISTENTE_123");
        } catch (IngressoInvalidoException e) {
            ingressoInvalidoLancado = true;
        }
        assertTrue(ingressoInvalidoLancado, "Deve lançar IngressoInvalidoException para código inexistente");
    }

    private static void testRecalculoReceitaRelatorio() {
        GerenciadorEventos g = new GerenciadorEventos();
        Evento show = new Show("Show Relatório", LocalDate.now().plusDays(5), "Concha Acústica", 10, 100.0, "Voz", "MPB");
        g.cadastrarEvento(show);

        Ingresso ing1 = g.venderIngresso(show.getId(), TipoIngresso.PISTA); // 100
        Ingresso ing2 = g.venderIngresso(show.getId(), TipoIngresso.VIP);   // 250
        g.venderIngresso(show.getId(), TipoIngresso.MEIA_ENTRADA);          // 50

        g.validarEntrada(ing1.getCodigo()); // 1 validado
        g.cancelarIngresso(ing2.getCodigo()); // 1 cancelado (estorno de 250)

        String relatorio = g.gerarRelatorio(show.getId());

        assertTrue(relatorio.contains("Ingressos Vendidos:    2"), "Vendidos ativos: 2 (Pista + Meia)");
        assertTrue(relatorio.contains("Entradas Validadas:    1"), "Validadas: 1");
        assertTrue(relatorio.contains("Ingressos Cancelados:  1"), "Cancelados: 1");
        assertTrue(relatorio.contains("RECEITA TOTAL:       R$ 150,00") || relatorio.contains("RECEITA TOTAL:       R$ 150.00"), "Receita líquida total: R$ 150,00");
    }

    private static void testEqualsEHashCodeModelos() {
        Evento evento1 = new Show("Festival Rock", LocalDate.now().plusDays(10), "Arena", 50, 100.0, "Banda A", "Rock");
        Evento evento2 = new Show("Festival Rock", LocalDate.now().plusDays(10), "Arena", 50, 100.0, "Banda A", "Rock");

        // Eventos com IDs diferentes não devem ser iguais
        assertFalse(evento1.equals(evento2), "Eventos com IDs distintos devem ser diferentes");
        assertTrue(evento1.equals(evento1), "Evento deve ser igual a si mesmo");

        Ingresso ing1 = evento1.venderIngresso(TipoIngresso.PISTA);
        Ingresso ing2 = evento1.venderIngresso(TipoIngresso.VIP);

        assertFalse(ing1.equals(ing2), "Ingressos com códigos distintos não devem ser iguais");
        assertTrue(ing1.equals(ing1), "Ingresso deve ser igual a si mesmo");

        java.util.Set<Ingresso> setIngressos = new java.util.HashSet<>();
        setIngressos.add(ing1);
        setIngressos.add(ing1);
        assertEquals(1, setIngressos.size(), "HashSet não deve duplicar ingressos iguais");
    }

    private static void testDashboardGeralETaxaOcupacao() {
        Evento.resetContadorId();
        GerenciadorEventos g = new GerenciadorEventos();

        Show show = new Show("Show Pop", LocalDate.now().plusDays(5), "Arena", 10, 100.0, "Cantor X", "Pop");
        Workshop workshop = new Workshop("Curso Kotlin", LocalDate.now().plusDays(10), "Lab", 10, 200.0, "Prof Y", 4);

        g.cadastrarEvento(show);
        g.cadastrarEvento(workshop);

        // Vender 5 ingressos no show (50% de ocupação)
        show.venderIngresso(TipoIngresso.PISTA); // R$ 100
        show.venderIngresso(TipoIngresso.PISTA); // R$ 100
        show.venderIngresso(TipoIngresso.VIP);   // R$ 250
        show.venderIngresso(TipoIngresso.VIP);   // R$ 250
        show.venderIngresso(TipoIngresso.MEIA_ENTRADA); // R$ 50 -> Total Show: R$ 750

        // Vender 10 ingressos no workshop (100% de ocupação, esgotado)
        for (int i = 0; i < 10; i++) {
            workshop.venderIngresso(TipoIngresso.PISTA); // 10 * 200 = R$ 2000
        }

        assertEqualsDouble(50.0, show.getTaxaOcupacao(), 0.001, "Taxa de ocupação do show deve ser 50%");
        assertFalse(show.isEsgotado(), "Show com 5/10 vagas não deve estar esgotado");

        assertEqualsDouble(100.0, workshop.getTaxaOcupacao(), 0.001, "Taxa de ocupação do workshop deve ser 100%");
        assertTrue(workshop.isEsgotado(), "Workshop com 10/10 vagas deve estar esgotado");

        assertEquals(15, g.getTotalIngressosVendidosGeral(), "Total de ingressos ativos vendidos na plataforma deve ser 15");
        assertEqualsDouble(2750.0, g.getReceitaTotalGeral(), 0.001, "Receita bruta consolidada deve ser R$ 2750.00");
        assertEqualsDouble(75.0, g.getTaxaOcupacaoMediaGeral(), 0.001, "Taxa média geral de ocupação deve ser 75% (15/20)");

        String dashboard = g.gerarDashboardGeral();
        assertTrue(dashboard.contains("DASHBOARD CONSOLIDADO"), "Dashboard deve conter cabeçalho");
        assertTrue(dashboard.contains("Total de Eventos Cadastrados: 2"), "Dashboard deve listar 2 eventos");
        assertTrue(dashboard.contains("Show Pop"), "Dashboard deve listar Show Pop");
        assertTrue(dashboard.contains("Curso Kotlin"), "Dashboard deve listar Curso Kotlin");
        assertTrue(dashboard.contains("[ESGOTADO]"), "Dashboard deve marcar evento esgotado");

        // Exportação do dashboard geral para TXT
        try {
            Path pathTemp = Files.createTempFile("teste_dashboard_", ".txt");
            Path gerado = g.exportarDashboardGeralTxt(pathTemp.toString());
            assertTrue(Files.exists(gerado), "Arquivo de dashboard deve existir");
            assertTrue(Files.size(gerado) > 0, "Arquivo de dashboard não deve estar vazio");
            Files.deleteIfExists(gerado);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao testar exportação do dashboard geral: " + e.getMessage(), e);
        }
    }
}
