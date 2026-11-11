package javaDeliveryPizza.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javaDeliveryPizza.domain.Pedido;
import javaDeliveryPizza.helpers.SaidaConsole;
import javaDeliveryPizza.util.AnalyticsHelper;

class CadastroEntradaIntegracaoTeste {

    @BeforeEach
    void reiniciarIds() {
        Cadastro.configurarProximoId(1);
    }

    @Test
    @DisplayName("IT-02: entradas invalidas digitadas no meio da venda sao recusadas e o cadastro conclui")
    void registrarVendaRecuperaDeEntradasInvalidas() {
        String entradas = String.join("\n",
                "segunda",    // dia da semana inválido -> EntradaDados pede de novo
                "1",          // dia da semana válido
                "1",          // status SERVIDO
                "5",          // avaliação
                "1",          // item: pizza
                "Calabresa",
                "quarenta",   // preço inválido -> EntradaDados pede de novo
                "40",         // preço válido
                "G",
                "2",          // quantidade
                "0",          // encerrar itens
                "");
        ArrayList<Pedido> pedidos = new ArrayList<>();

        String saida = SaidaConsole.capturar(() -> Cadastro.registrarVenda(new Scanner(entradas), pedidos));

        Pedido pedido = pedidos.get(0);
        assertAll(
            () -> assertEquals(1, pedidos.size()),
            () -> assertEquals(1, pedido.getDiaSemana()),
            () -> assertEquals("SERVIDO", pedido.getStatus()),
            () -> assertEquals(1, pedido.getItens().size()),
            () -> assertEquals(80.0, AnalyticsHelper.valorTotal(pedido), 0.001),
            () -> assertTrue(saida.contains("Valor inválido"))
        );
    }
}
