package javaDeliveryPizza.Utils;

import javaDeliveryPizza.domain.Bebida;
import javaDeliveryPizza.domain.ItemProduto;
import javaDeliveryPizza.domain.Pedido;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javaDeliveryPizza.domain.Pizza;
import javaDeliveryPizza.util.AnalyticsHelper;
import javaDeliveryPizza.util.ListagemUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ListagemUtilsIntergracaoTeste {

    private PrintStream originalOut;
    private ByteArrayOutputStream outputStream;

    private void iniciarCapturaSaida() {
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    @DisplayName("IT-14A: Saida de listarPedidos caso a avaliação for 0")
    public void saidaListarPedidosAval0() {
        ArrayList<Pedido> pedidos = new ArrayList<Pedido>();
        Pedido p = new Pedido(1);
        p.setStatus("SERVIDO");
        p.setDiaDaSemana(1);
        p.setAvaliacao(0);
        p.addItem(new ItemProduto(new Pizza("Calabresa", "G", 40.00), 2));
        pedidos.add(p);

        iniciarCapturaSaida();

        try {
            ListagemUtils.listarPedidos(pedidos);

            double valor = AnalyticsHelper.arred2(AnalyticsHelper.valorTotal(p));
            String dia = AnalyticsHelper.nomeDia(p.getDiaSemana());

            String[] linhas = outputStream.toString().split(System.lineSeparator());

            String resultadoDescricaoPedido = "#" + p.getNumPedido() + " | Dia " + dia
                    + " | " + p.getStatus()
                    + " | Aval=-"
                    + " | Total=R$ " + valor;
            assertAll("Pedido com avaliação sendo 0",
                    () -> assertEquals(resultadoDescricaoPedido, linhas[2]),
                    () -> assertEquals("- Pizza Calabresa (G) x2", linhas[3].trim()));

        } finally {
            System.setOut(originalOut);

        }
    }

    @Test
    @DisplayName("IT-14B: Saida de listarPedidos Caso a avaliação for maior que 0")
    public void saidaListarPedidosAvalMaior0() {
        ArrayList<Pedido> pedidos = new ArrayList<Pedido>();
        Pedido p = new Pedido(1);
        p.setStatus("SERVIDO");
        p.setDiaDaSemana(1);
        p.setAvaliacao(4);
        p.addItem(new ItemProduto(new Pizza("Calabresa", "G", 40.00), 2));
        pedidos.add(p);

        iniciarCapturaSaida();
        try {
            ListagemUtils.listarPedidos(pedidos);

            double valor = AnalyticsHelper.arred2(AnalyticsHelper.valorTotal(p));
            String dia = AnalyticsHelper.nomeDia(p.getDiaSemana());

            String[] linhas = outputStream.toString().split(System.lineSeparator());

            String resultadoDescricaoPedido = "#" + p.getNumPedido() + " | Dia " + dia
                    + " | " + p.getStatus()
                    + " | Aval=" + p.getAvaliacao()
                    + " | Total=R$ " + valor;
            assertAll("Pedido com avaliação sendo 4",
                    () -> assertEquals(resultadoDescricaoPedido, linhas[2]),
                    () -> assertEquals("- Pizza Calabresa (G) x2", linhas[3].trim()));

        } finally {
            System.setOut(originalOut);

        }
    }

    @Test
    @DisplayName("IT-14C: listarPedidos com pedido Cancelado")
    public void saidaListarPedidosPedidoCancelaod() {
        ArrayList<Pedido> pedidos = new ArrayList<Pedido>();
        Pedido p = new Pedido(1);
        p.setStatus("CANCELADO");
        p.setMotivoCancelamento("Cliente desistiu");
        p.setDiaDaSemana(1);
        p.setAvaliacao(3);
        p.addItem(new ItemProduto(new Pizza("Calabresa", "G", 40.00), 2));
        pedidos.add(p);

        iniciarCapturaSaida();
        try {
            ListagemUtils.listarPedidos(pedidos);

            double valor = AnalyticsHelper.arred2(AnalyticsHelper.valorTotal(p));
            String dia = AnalyticsHelper.nomeDia(p.getDiaSemana());

            String resultadoDescricaoPedido = "#" + p.getNumPedido() + " | Dia " + dia
                    + " | " + p.getStatus()
                    + " | Aval=" + p.getAvaliacao()
                    + " | Total=R$ " + valor;

            String[] linhas = outputStream.toString().split(System.lineSeparator());
            assertAll("Pedido Cancelado.",
                    () -> assertEquals(resultadoDescricaoPedido, linhas[2]),
                    () -> assertEquals("Motivo: Cliente desistiu", linhas[3].trim()));

        } finally {
            System.setOut(originalOut);

        }

    }

    @Test
    @DisplayName("IT-14D: listarPedidos com mais de um pedido na mesma lista")
    public void saidaListarPedidosVariosPedidos() {
        ArrayList<Pedido> pedidos = new ArrayList<Pedido>();
        Pedido p1 = new Pedido(2);
        Pedido p2 = new Pedido(2);

        p1.setStatus("SERVIDO");
        p1.setDiaDaSemana(2);
        p1.setAvaliacao(4);
        p1.addItem(new ItemProduto(new Pizza("Calabresa", "G", 40.00), 2));

        p2.setStatus("SERVIDO");
        p2.setDiaDaSemana(7);
        p2.setAvaliacao(3);
        p2.addItem(new ItemProduto(new Bebida("Coca Cola", "Lata", 5.00), 2));

        pedidos.add(p1);
        pedidos.add(p2);

        iniciarCapturaSaida();
        try {
            ListagemUtils.listarPedidos(pedidos);

            double valorPedido1 = AnalyticsHelper.arred2(AnalyticsHelper.valorTotal(p1));
            String diaPedido1 = AnalyticsHelper.nomeDia(p1.getDiaSemana());

            String resultadoDescricaoPedido1 = "#" + p1.getNumPedido() + " | Dia " + diaPedido1
                    + " | " + p1.getStatus()
                    + " | Aval=" + p1.getAvaliacao()
                    + " | Total=R$ " + valorPedido1;

            double valorPedido2 = AnalyticsHelper.arred2(AnalyticsHelper.valorTotal(p2));
            String diaPedido2 = AnalyticsHelper.nomeDia(p2.getDiaSemana());

            String resultadoDescricaoPedido2 = "#" + p2.getNumPedido() + " | Dia " + diaPedido2
                    + " | " + p2.getStatus()
                    + " | Aval=" + p2.getAvaliacao()
                    + " | Total=R$ " + valorPedido2;

            String[] linhas = outputStream.toString().split(System.lineSeparator());

            assertAll("Lista de pedidos",
                    () -> assertEquals(resultadoDescricaoPedido1, linhas[2]),
                    () -> assertEquals("- Pizza Calabresa (G) x2", linhas[3].trim()),
                    () -> assertEquals(resultadoDescricaoPedido2, linhas[4]),
                    () -> assertEquals("- Bebida Coca Cola (Lata) x2", linhas[5].trim()));

        } finally {
            System.setOut(originalOut);

        }
    }

}
