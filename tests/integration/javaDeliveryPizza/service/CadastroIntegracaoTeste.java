package javaDeliveryPizza.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javaDeliveryPizza.domain.Pedido;
import javaDeliveryPizza.helpers.SaidaConsole;

class CadastroIntegracaoTeste {

    @BeforeEach
    void reiniciarIds() {
        Cadastro.configurarProximoId(1);
    }

    @Test
    @DisplayName("IT-05: venda cancelada é registrada sem itens, com o motivo aparado e avaliação zerada")
    void registrarVendaCanceladaComMotivo() {
        String entradas = String.join("\n","3","2","  Cliente desistiu  ","");
        ArrayList<Pedido> pedidos = new ArrayList<>();

        String saida = SaidaConsole.capturar(() -> Cadastro.registrarVenda(new Scanner(entradas), pedidos));

        Pedido pedido = pedidos.get(0);
        assertAll(
            () -> assertEquals(1, pedidos.size()),
            () -> assertEquals(1, pedido.getNumPedido()),
            () -> assertEquals(3, pedido.getDiaSemana()),
            () -> assertEquals("CANCELADO", pedido.getStatus()),
            () -> assertEquals("Cliente desistiu", pedido.getMotivoCancelamento()),
            () -> assertEquals(0, pedido.getAvaliacao()),
            () -> assertTrue(pedido.getItens().isEmpty()),
            () -> assertTrue(saida.contains("CANCELADA registrada"))
        );
    }

    @Test
    @DisplayName("IT-06: motivo de cancelamento em branco é gravado como \"Não informado\"")
    void registrarVendaCanceladaSemMotivoGravaTextoPadrao() {
        String entradas = String.join("\n","5","2","   ","");
        ArrayList<Pedido> pedidos = new ArrayList<>();

        SaidaConsole.capturar(() -> Cadastro.registrarVenda(new Scanner(entradas), pedidos));

        assertAll(
            () -> assertEquals(1, pedidos.size()),
            () -> assertEquals("CANCELADO", pedidos.get(0).getStatus()),
            () -> assertEquals("Não informado", pedidos.get(0).getMotivoCancelamento())
        );
    }

    @Test
    @DisplayName("IT-07: removerVenda retira apenas o pedido do número informado e preserva os demais")
    void removerVendaRemoveApenasOPedidoAlvo() {
        Pedido primeiro = new Pedido(1);
        Pedido alvo = new Pedido(2);
        Pedido ultimo = new Pedido(3);
        ArrayList<Pedido> pedidos = new ArrayList<>();
        pedidos.add(primeiro);
        pedidos.add(alvo);
        pedidos.add(ultimo);

        boolean removido = Cadastro.removerVenda(pedidos, 2);

        assertAll(
            () -> assertTrue(removido),
            () -> assertEquals(2, pedidos.size()),
            () -> assertSame(primeiro, pedidos.get(0)),
            () -> assertSame(ultimo, pedidos.get(1))
        );
    }

    @Test
    @DisplayName("IT-08: removerVenda devolve false e não altera a lista quando o número não existe")
    void removerVendaDeNumeroInexistenteNaoAlteraALista() {
        Pedido primeiro = new Pedido(1);
        Pedido segundo = new Pedido(2);
        ArrayList<Pedido> pedidos = new ArrayList<>();
        pedidos.add(primeiro);
        pedidos.add(segundo);

        boolean removido = Cadastro.removerVenda(pedidos, 3);

        assertAll(
            () -> assertFalse(removido),
            () -> assertEquals(2, pedidos.size()),
            () -> assertSame(primeiro, pedidos.get(0)),
            () -> assertSame(segundo, pedidos.get(1))
        );
    }

    @Test
    @DisplayName("IT-09: cada venda registrada consome exatamente um número, na ordem de chegada")
    void vendasSeguidasRecebemNumerosSequenciais() {
        String umaVenda = String.join("\n","1","1","0","1","Calabresa","40","G","1","0","");
        ArrayList<Pedido> pedidos = new ArrayList<>();

        SaidaConsole.capturar(() -> {
            Cadastro.registrarVenda(new Scanner(umaVenda), pedidos);
            Cadastro.registrarVenda(new Scanner(umaVenda), pedidos);
        });

        assertAll(
            () -> assertEquals(2, pedidos.size()),
            () -> assertEquals(1, pedidos.get(0).getNumPedido()),
            () -> assertEquals(2, pedidos.get(1).getNumPedido())
        );
    }
}
