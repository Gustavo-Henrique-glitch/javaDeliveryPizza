package javaDeliveryPizza.util;

import javaDeliveryPizza.domain.Bebida;
import javaDeliveryPizza.domain.Pedido;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javaDeliveryPizza.domain.Pizza;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ListagemUtilsUnitTeste {


    @Test
    @DisplayName("UT-36A: nomeProduto completo")
    public void nomeDoProdutoCompletoComTamanho() {
        Pizza pi = new Pizza("Calabresa", "G", 30);
        Bebida be = new Bebida("Coca Cola", "Lata", 1);

        assertAll("Pedidos com caracteristicas completas",
                () -> assertEquals("Pizza Calabresa (G)", ListagemUtils.nomeProduto(pi)),
                () -> assertEquals("Bebida Coca Cola (Lata)", ListagemUtils.nomeProduto(be)));
    }

    @Test
    @DisplayName("UT-36B: nomeProduto nulo")
    public void produtoNulo() {
        String resposta = ListagemUtils.nomeProduto(null);
        assertEquals("Produto", resposta);
    }

    @Test
    @DisplayName("UT-37: titulo de listarPedidos e caso a lista esteja vazia")
    public void primeriasLinhasListarPedidos() {
        ArrayList<Pedido> pedidos = new ArrayList<>();

        // Captura de linhas
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        String tituloEsperado = "--- Vendas do restaurante ---";
        try {
            ListagemUtils.listarPedidos(pedidos);
            String[] linhas = outputStream.toString().split(System.lineSeparator());
            if (pedidos.size() == 0) {
                assertAll("Primerias linhas de listarPedidos",
                        () -> assertEquals(tituloEsperado, linhas[1]),
                        () -> assertEquals("(vazio)", linhas[2]));
            }
        }
        finally
        {
            System.setOut(originalOut);
        }
    }

  

}
