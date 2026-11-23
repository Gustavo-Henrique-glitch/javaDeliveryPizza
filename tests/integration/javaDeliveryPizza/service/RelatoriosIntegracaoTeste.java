package javaDeliveryPizza.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javaDeliveryPizza.domain.Bebida;
import javaDeliveryPizza.domain.Evento;
import javaDeliveryPizza.domain.ItemProduto;
import javaDeliveryPizza.domain.Pedido;
import javaDeliveryPizza.domain.Pizza;
import javaDeliveryPizza.helpers.SaidaConsole;

class RelatoriosIntegracaoTeste {

    private static final String LINHA_FATURAMENTO = "Faturamento consolidado:";

    @Test
    @DisplayName("IT-04: faturamento consolidado ignora buffet de evento cancelado (F-01)")
    void faturamentoIgnoraEventoCancelado() {
        ArrayList<Pedido> pedidos = new ArrayList<>();
        pedidos.add(vendaServida(10.0));

        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(evento(1, "REALIZADO", 100.0, 2, 50.0));  // buffet 100 + ingressos 100
        eventos.add(evento(2, "CANCELADO", 1000.0, 0, 50.0)); // não pode gerar receita

        String saida = SaidaConsole.capturar(() -> Relatorios.exibirRelatorioCompleto(pedidos, eventos));

        assertEquals(LINHA_FATURAMENTO + " R$ 210.0", linhaDeFaturamento(saida));
    }

    @Test
    @DisplayName("IT-23: ticket médio do salão é a média das vendas SERVIDAS")
    void ticketMedioSalaoIgnoraVendasCanceladas() {
        Pedido cancelado = new Pedido(3);
        cancelado.setStatus("CANCELADO");
        cancelado.setDiaDaSemana(3);
        cancelado.addItem(new ItemProduto(new Bebida("Agua", "Lata", 999.0), 1));

        ArrayList<Pedido> pedidos = new ArrayList<>();
        pedidos.add(vendaServida(10.0));
        pedidos.add(vendaServida(30.0));
        pedidos.add(cancelado);

        String saida = SaidaConsole.capturar(() -> Relatorios.exibirRelatorioCompleto(pedidos, new ArrayList<>()));

        assertTrue(saida.contains("   Salão: R$ 20.0"), saida);
    }

    @Test
    @DisplayName("IT-24: sem venda servida, o ticket médio do salão é zero")
    void ticketMedioSalaoSemVendasServidasEhZero() {
        Pedido cancelado1 = new Pedido(1);
        cancelado1.setStatus("CANCELADO");
        cancelado1.setDiaDaSemana(3);
        cancelado1.addItem(new ItemProduto(new Bebida("Agua", "Lata", 50.0), 1));

        Pedido cancelado2 = new Pedido(2);
        cancelado2.setStatus("CANCELADO");
        cancelado2.setDiaDaSemana(4);
        cancelado2.addItem(new ItemProduto(new Bebida("Agua", "Lata", 70.0), 1));

        ArrayList<Pedido> pedidos = new ArrayList<>();
        pedidos.add(cancelado1);
        pedidos.add(cancelado2);

        String saida = SaidaConsole.capturar(() -> Relatorios.exibirRelatorioCompleto(pedidos, new ArrayList<>()));

        assertTrue(saida.contains("   Salão: R$ 0.0"), saida);
    }

    @Test
    @DisplayName("IT-25: ticket médio dos eventos conta só os realizados com buffet")
    void ticketMedioEventosIgnoraRealizadoSemBuffet() {
        Evento semBuffet = new Evento(3);
        semBuffet.setNome("Sem buffet");
        semBuffet.setTipo("Show");
        semBuffet.setDiaSemana(3);
        semBuffet.setStatus("REALIZADO");

        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(evento(1, "REALIZADO", 100.0, 2, 50.0));
        eventos.add(evento(2, "REALIZADO", 40.0, 0, 50.0)); 
        eventos.add(semBuffet);                    
        eventos.add(evento(4, "CANCELADO", 500.0, 9, 50.0));

        String saida = SaidaConsole.capturar(() -> Relatorios.exibirRelatorioCompleto(new ArrayList<>(), eventos));
        assertTrue(saida.contains("   Eventos: R$ 70.0"), saida);
    }

    @Test
    @DisplayName("IT-26: ticket médio combinado divide pelo total de entradas, não pela média das médias")
    void ticketMedioCombinadoEhMediaPonderada() {
        ArrayList<Pedido> pedidos = new ArrayList<>();
        pedidos.add(vendaServida(10.0));
        pedidos.add(vendaServida(30.0));

        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(evento(1, "REALIZADO", 60.0, 0, 0.0));

        String saida = SaidaConsole.capturar(() -> Relatorios.exibirRelatorioCompleto(pedidos, eventos));


        assertTrue(saida.contains("   Combinado: R$ 33.33"), saida);
    }

    @Test
    @DisplayName("IT-27: satisfação média ignora nota 0 e registros não realizados")
    void satisfacaoMediaIgnoraNotaZeroENaoRealizados() {
        Pedido nota5 = vendaServida(10.0);
        nota5.setAvaliacao(5);

        Pedido nota4 = vendaServida(10.0);
        nota4.setAvaliacao(4);

        Pedido semNota = vendaServida(10.0); 

        Pedido cancelado = new Pedido(4);
        cancelado.setStatus("CANCELADO");
        cancelado.setAvaliacao(5);

        ArrayList<Pedido> pedidos = new ArrayList<>();
        pedidos.add(nota5);
        pedidos.add(nota4);
        pedidos.add(semNota);
        pedidos.add(cancelado);

        Evento eventoNota5 = evento(1, "REALIZADO", 100.0, 2, 50.0);
        eventoNota5.setAvaliacao(5);

        Evento eventoNota3 = evento(2, "REALIZADO", 100.0, 2, 50.0);
        eventoNota3.setAvaliacao(3);

        Evento eventoCancelado = evento(3, "CANCELADO", 0.0, 0, 50.0);
        eventoCancelado.setAvaliacao(5);

        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(eventoNota5);
        eventos.add(eventoNota3);
        eventos.add(eventoCancelado);

        String saida = SaidaConsole.capturar(() -> Relatorios.exibirRelatorioCompleto(pedidos, eventos));

        assertTrue(saida.contains("   Salão: 4.5 / 5"), saida);       
        assertTrue(saida.contains("   Eventos: 4.0 / 5"), saida);    
        assertTrue(saida.contains("   Combinado: 4.25 / 5"), saida);  
    }

    @Test
    @DisplayName("IT-28: produto mais consumido do salão soma quantidades e ignora pedido cancelado")
    void produtoMaisConsumidoDoSalaoSomaQuantidades() {
        Pedido pedido1 = new Pedido(1);
        pedido1.setStatus("SERVIDO");
        pedido1.setDiaDaSemana(3);
        pedido1.addItem(new ItemProduto(new Pizza("Calabresa", "G", 40.0), 2));
        pedido1.addItem(new ItemProduto(new Bebida("Agua", "Lata", 5.0), 1));

        Pedido pedido2 = new Pedido(2);
        pedido2.setStatus("SERVIDO");
        pedido2.setDiaDaSemana(3);
        pedido2.addItem(new ItemProduto(new Pizza("Calabresa", "G", 40.0), 1));

        Pedido cancelado = new Pedido(3);
        cancelado.setStatus("CANCELADO");
        cancelado.setDiaDaSemana(3);
        cancelado.addItem(new ItemProduto(new Pizza("Mussarela", "G", 40.0), 10));

        ArrayList<Pedido> pedidos = new ArrayList<>();
        pedidos.add(pedido1);
        pedidos.add(pedido2);
        pedidos.add(cancelado);

        String saida = SaidaConsole.capturar(() -> Relatorios.exibirRelatorioCompleto(pedidos, new ArrayList<>()));

        assertTrue(saida.contains("   Salão: Pizza Calabresa (G) (3)"), saida);
        assertFalse(saida.contains("Mussarela"), saida);
    }

    @Test
    @DisplayName("IT-29: consumo combinado soma salão e buffet do evento do mesmo dia")
    void consumoCombinadoSomaSalaoEEventos() {
        Pedido pedido = new Pedido(1);
        pedido.setStatus("SERVIDO");
        pedido.setDiaDaSemana(2);
        pedido.addItem(new ItemProduto(new Pizza("Calabresa", "G", 40.0), 1));

        Evento evento = new Evento(1);
        evento.setNome("Festa");
        evento.setTipo("Show");
        evento.setDiaSemana(2);
        evento.setStatus("AGENDADO");
        evento.adicionarItemBuffet(new ItemProduto(new Pizza("Calabresa", "G", 40.0), 3));

        ArrayList<Pedido> pedidos = new ArrayList<>();
        pedidos.add(pedido);
        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(evento);

        String saida = SaidaConsole.capturar(() -> Relatorios.exibirRelatorioCompleto(pedidos, eventos));

        assertTrue(saida.contains("   Salão: Pizza Calabresa (G) (1)"), saida);
        assertTrue(saida.contains("   Eventos: Pizza Calabresa (G) (3)"), saida);
        assertTrue(saida.contains("   Combinado: Pizza Calabresa (G) (4)"), saida);
    }

    @Test
    @DisplayName("IT-30: dia só com evento marca salão como vazio e descarta buffet de evento cancelado")
    void diaSoComEventoMarcaSalaoVazio() {
        Evento realizado = new Evento(1);
        realizado.setNome("Festa");
        realizado.setTipo("Show");
        realizado.setDiaSemana(2);
        realizado.setStatus("REALIZADO");
        realizado.adicionarItemBuffet(new ItemProduto(new Pizza("Calabresa", "G", 40.0), 1));

        Evento cancelado = new Evento(2);
        cancelado.setNome("Festa cancelada");
        cancelado.setTipo("Show");
        cancelado.setDiaSemana(2);
        cancelado.setStatus("CANCELADO");
        cancelado.adicionarItemBuffet(new ItemProduto(new Pizza("Mussarela", "G", 40.0), 9));

        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(realizado);
        eventos.add(cancelado);

        String saida = SaidaConsole.capturar(() -> Relatorios.exibirRelatorioCompleto(new ArrayList<>(), eventos));

        assertTrue(saida.contains("   Salão: —"), saida);
        assertTrue(saida.contains("   Eventos: Pizza Calabresa (G) (1)"), saida);
        assertFalse(saida.contains("Mussarela"), saida);
    }

    @Test
    @DisplayName("IT-31: top 3 sabores soma salão e eventos e sai em ordem decrescente")
    void topSaboresSomaSalaoEEventosEmOrdem() {
        Pedido pedido = new Pedido(1);
        pedido.setStatus("SERVIDO");
        pedido.setDiaDaSemana(3);
        pedido.addItem(new ItemProduto(new Pizza("Calabresa", "G", 40.0), 2));
        pedido.addItem(new ItemProduto(new Pizza("Mussarela", "G", 40.0), 1));

        ArrayList<Pedido> pedidos = new ArrayList<>();
        pedidos.add(pedido);

        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(evento(1, "REALIZADO", 40.0, 2, 50.0));

        Evento portuguesa = new Evento(2);
        portuguesa.setNome("Festa");
        portuguesa.setTipo("Show");
        portuguesa.setDiaSemana(3);
        portuguesa.setStatus("REALIZADO");
        portuguesa.adicionarItemBuffet(new ItemProduto(new Pizza("Portuguesa", "G", 40.0), 4));
        eventos.add(portuguesa);

        String saida = SaidaConsole.capturar(() -> Relatorios.exibirRelatorioCompleto(pedidos, eventos));

        assertTrue(saida.contains("1) Portuguesa — 4"), saida);
        assertTrue(saida.contains("2) Calabresa — 3"), saida);
        assertTrue(saida.contains("3) Mussarela — 1"), saida);
    }

    @Test
    @DisplayName("IT-32: top 3 mostra só as posições com dados e avisa quando não há nenhum sabor")
    void topSaboresMostraApenasPosicoesComDados() {
        Pedido pedido = new Pedido(1);
        pedido.setStatus("SERVIDO");
        pedido.setDiaDaSemana(3);
        pedido.addItem(new ItemProduto(new Pizza("Calabresa", "G", 40.0), 2));
        pedido.addItem(new ItemProduto(new Pizza("Mussarela", "G", 40.0), 1));

        ArrayList<Pedido> pedidos = new ArrayList<>();
        pedidos.add(pedido);

        String comDoisSabores = SaidaConsole.capturar(() -> Relatorios.exibirRelatorioCompleto(pedidos, new ArrayList<>()));
        String semDados = SaidaConsole.capturar(() -> Relatorios.exibirRelatorioCompleto(new ArrayList<>(), new ArrayList<>()));

        assertTrue(comDoisSabores.contains("1) Calabresa — 2"), comDoisSabores);
        assertTrue(comDoisSabores.contains("2) Mussarela — 1"), comDoisSabores);
        assertFalse(comDoisSabores.contains("\n3) "), comDoisSabores);
        assertTrue(semDados.contains("(sem dados)"), semDados);
    }

    @Test
    @DisplayName("IT-33: adesão ao buffet conta só realizados e calcula percentual e médias sobre eles")
    void adesaoAoBuffetCalculaPercentualEMedias() {
        Evento comBuffet2Itens = new Evento(1);
        comBuffet2Itens.setNome("Festa A");
        comBuffet2Itens.setTipo("Show");
        comBuffet2Itens.setDiaSemana(3);
        comBuffet2Itens.setStatus("REALIZADO");
        comBuffet2Itens.adicionarItemBuffet(new ItemProduto(new Pizza("Calabresa", "G", 50.0), 2));

        Evento comBuffet1Item = new Evento(2);
        comBuffet1Item.setNome("Festa B");
        comBuffet1Item.setTipo("Show");
        comBuffet1Item.setDiaSemana(3);
        comBuffet1Item.setStatus("REALIZADO");
        comBuffet1Item.adicionarItemBuffet(new ItemProduto(new Pizza("Mussarela", "G", 30.0), 1));

        Evento semBuffet = new Evento(3);
        semBuffet.setNome("Festa C");
        semBuffet.setTipo("Show");
        semBuffet.setDiaSemana(3);
        semBuffet.setStatus("REALIZADO");

        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(comBuffet2Itens);
        eventos.add(comBuffet1Item);
        eventos.add(semBuffet);
        eventos.add(evento(4, "CANCELADO", 999.0, 5, 50.0));

        String saida = SaidaConsole.capturar(() -> Relatorios.exibirRelatorioCompleto(new ArrayList<>(), eventos));

        assertTrue(saida.contains("Eventos realizados: 3"), saida);
        assertTrue(saida.contains("Eventos com buffet do restaurante: 2 (66.67%)"), saida);
        assertTrue(saida.contains("Receita média por buffet contratado: R$ 65.0"), saida);
        assertTrue(saida.contains("Itens médios fornecidos pelo restaurante: 1.5"), saida);
    }

    @Test
    @DisplayName("IT-34: adesão ao buffet avisa quando não há evento realizado e quando nenhum contratou buffet")
    void adesaoAoBuffetAvisaCasosSemDados() {
        String semRealizados = SaidaConsole.capturar(() -> Relatorios.exibirRelatorioCompleto(new ArrayList<>(), new ArrayList<>()));

        assertTrue(semRealizados.contains("Não há eventos realizados para análise."), semRealizados);

        Evento semBuffet = new Evento(1);
        semBuffet.setNome("Festa");
        semBuffet.setTipo("Show");
        semBuffet.setDiaSemana(3);
        semBuffet.setStatus("REALIZADO");

        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(semBuffet);

        String semBuffetContratado = SaidaConsole.capturar(() -> Relatorios.exibirRelatorioCompleto(new ArrayList<>(), eventos));

        assertTrue(semBuffetContratado.contains("Eventos com buffet do restaurante: 0 (0.0%)"), semBuffetContratado);
        assertTrue(semBuffetContratado.contains("Nenhum evento realizado contratou o buffet do restaurante."), semBuffetContratado);
    }

    @Test
    @DisplayName("IT-35: faturamento do salão em dia de evento conta só vendas servidas desses dias")
    void faturamentoDoSalaoEmDiasDeEvento() {
        Pedido vendaDiaComEvento = vendaServida(40.0);
        vendaDiaComEvento.setDiaDaSemana(2);

        Pedido vendaDiaSemEvento = vendaServida(100.0);
        vendaDiaSemEvento.setDiaDaSemana(4);

        Pedido canceladaNoDiaDoEvento = vendaServida(500.0);
        canceladaNoDiaDoEvento.setStatus("CANCELADO");
        canceladaNoDiaDoEvento.setDiaDaSemana(5);

        ArrayList<Pedido> pedidos = new ArrayList<>();
        pedidos.add(vendaDiaComEvento);
        pedidos.add(vendaDiaSemEvento);
        pedidos.add(canceladaNoDiaDoEvento);

        Evento eventoDia2 = evento(1, "AGENDADO", 100.0, 0, 50.0);
        eventoDia2.setDiaSemana(2);

        Evento eventoDia5 = evento(2, "REALIZADO", 100.0, 1, 50.0);
        eventoDia5.setDiaSemana(5);

        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(eventoDia2);
        eventos.add(eventoDia5);

        String saida = SaidaConsole.capturar(() -> Relatorios.exibirRelatorioCompleto(pedidos, eventos));

        assertTrue(saida.contains("Total de vendas analisadas: 1"), saida);
        assertTrue(saida.contains("Faturamento acumulado no salão: R$ 40.0"), saida);

        ArrayList<Pedido> soDiaSemEvento = new ArrayList<>();
        soDiaSemEvento.add(vendaDiaSemEvento);

        String semVendas = SaidaConsole.capturar(() -> Relatorios.exibirRelatorioCompleto(soDiaSemEvento, eventos));

        assertTrue(semVendas.contains("O salão não registrou vendas nos dias com eventos."), semVendas);
    }

    @Test
    @DisplayName("IT-36: evento de maior buffet é o de maior valor total, ignorando buffet vazio")
    void eventoMaiorBuffetEscolheMaiorValor() {
        Evento buffet100 = evento(1, "REALIZADO", 100.0, 2, 50.0);

        Evento buffet250 = new Evento(2);
        buffet250.setNome("Formatura");
        buffet250.setTipo("Formatura");
        buffet250.setDiaSemana(3);
        buffet250.setStatus("REALIZADO");
        buffet250.adicionarItemBuffet(new ItemProduto(new Pizza("Calabresa", "G", 125.0), 2));

        Evento semBuffet = new Evento(3);
        semBuffet.setNome("Sem buffet");
        semBuffet.setTipo("Show");
        semBuffet.setDiaSemana(3);
        semBuffet.setStatus("REALIZADO");

        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(buffet100);
        eventos.add(buffet250);
        eventos.add(semBuffet);

        String saida = SaidaConsole.capturar(() -> Relatorios.exibirRelatorioCompleto(new ArrayList<>(), eventos));

        assertTrue(saida.contains("Evento #2 - Formatura"), saida);
        assertTrue(saida.contains("Tipo: Formatura | Dia: Qua"), saida);
        assertTrue(saida.contains("Valor total do buffet: R$ 250.0"), saida);
    }

    @Test
    @DisplayName("IT-37: receita combinada por dia separa salão e eventos e ignora evento não realizado")
    void receitaCombinadaPorDiaSeparaSalaoEEventos() {
        ArrayList<Pedido> pedidos = new ArrayList<>();
        pedidos.add(vendaServida(60.0));

        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(evento(1, "REALIZADO", 90.0, 1, 50.0)); 
        eventos.add(evento(2, "CANCELADO", 500.0, 9, 50.0));

        String saida = SaidaConsole.capturar(() -> Relatorios.exibirRelatorioCompleto(pedidos, eventos));

        assertTrue(saida.contains("Qua: R$ 200.0 | Salão: R$ 60.0 (30.0%) | Eventos: R$ 140.0 (70.0%) | Eventos realizados: 1"), saida);
    }

    private static Pedido vendaServida(double valor) {
        Pedido pedido = new Pedido(1);
        pedido.setStatus("SERVIDO");
        pedido.setDiaDaSemana(3);
        pedido.addItem(new ItemProduto(new Bebida("Agua", "Lata", valor), 1));
        return pedido;
    }

    private static Evento evento(int id, String status, double valorBuffet, int ingressos, double precoIngresso) {
        Evento evento = new Evento(id);
        evento.setNome("Evento " + id);
        evento.setTipo("Show");
        evento.setDiaSemana(3);
        evento.setStatus(status);
        evento.setPrecoIngresso(precoIngresso);
        evento.setIngressosVendidos(ingressos);
        evento.adicionarItemBuffet(new ItemProduto(new Pizza("Calabresa", "G", valorBuffet), 1));
        return evento;
    }

    private static String linhaDeFaturamento(String saida) {
        return saida.lines()
                .filter(linha -> linha.startsWith(LINHA_FATURAMENTO))
                .findFirst()
                .orElse("(linha de faturamento ausente)");
    }
}
