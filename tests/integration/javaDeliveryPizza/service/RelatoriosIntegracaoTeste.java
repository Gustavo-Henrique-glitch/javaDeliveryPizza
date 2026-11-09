package javaDeliveryPizza.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
