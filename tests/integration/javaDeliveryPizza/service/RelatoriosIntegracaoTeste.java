package javaDeliveryPizza.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        Pedido semNota = vendaServida(10.0); // avaliação 0: fica fora

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
