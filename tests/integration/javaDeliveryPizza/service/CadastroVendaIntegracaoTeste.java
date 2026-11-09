package javaDeliveryPizza.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javaDeliveryPizza.domain.Pedido;
import javaDeliveryPizza.helpers.SaidaConsole;
import javaDeliveryPizza.util.AnalyticsHelper;

class CadastroVendaIntegracaoTeste {

    @BeforeEach
    void reiniciarIds() {
        Cadastro.configurarProximoId(1);
    }

    @Test
    @DisplayName("IT-01: venda digitada passa por EntradaDados e chega completa à lista")
    void registrarVendaValidaComDoisItens() {
        String entradas = String.join("\n",
                "1",          // dia da semana
                "1",          // status SERVIDO
                "5",          // avaliação
                "1",          // item: pizza
                "Calabresa",
                "40",
                "G",
                "2",          // quantidade
                "1",          // adicionar outro item
                "2",          // item: bebida
                "Cola",
                "8",
                "Lata",
                "3",          // quantidade
                "0",          // encerrar itens
                "");
        ArrayList<Pedido> pedidos = new ArrayList<>();

        SaidaConsole.capturar(() -> Cadastro.registrarVenda(new Scanner(entradas), pedidos));

        assertEquals(1, pedidos.size());
        Pedido pedido = pedidos.get(0);
        assertEquals(1, pedido.getNumPedido());
        assertEquals("SERVIDO", pedido.getStatus());
        assertEquals(5, pedido.getAvaliacao());
        assertEquals(2, pedido.getItens().size());
        assertEquals(104.0, AnalyticsHelper.valorTotal(pedido), 0.001);
    }
}
