package javaDeliveryPizza.domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PedidoTeste {

    @Test
    @DisplayName("UT-29: pedido recém-criado começa sem itens, sem avaliação e sem motivo de cancelamento")
    void pedidoRecemCriadoComecaNeutro() {
        Pedido pedido = new Pedido(7);

        assertAll(
            () -> assertEquals(7, pedido.getNumPedido()),
            () -> assertEquals(0, pedido.getAvaliacao()),
            () -> assertEquals("", pedido.getMotivoCancelamento()),
            () -> assertTrue(pedido.getItens().isEmpty()),
            () -> assertNull(pedido.getStatus())
        );
    }

    @Test
    @DisplayName("UT-30: addItem acumula os itens na ordem de inserção, sem substituir os anteriores")
    void addItemAcumulaItensNaOrdemDeInsercao() {
        Pedido pedido = new Pedido(1);
        ItemProduto pizzaCalabresa = new ItemProduto(new Pizza("Calabresa", "G", 45.0), 1);
        ItemProduto pizzaMargherita = new ItemProduto(new Pizza("Margherita", "M", 39.0), 2);
        ItemProduto refrigerante = new ItemProduto(new Bebida("Refrigerante", "2L", 12.0), 3);

        pedido.addItem(pizzaCalabresa);
        pedido.addItem(pizzaMargherita);
        pedido.addItem(refrigerante);

        assertAll(
            () -> assertEquals(3, pedido.getItens().size()),
            () -> assertSame(pizzaCalabresa, pedido.getItens().get(0)),
            () -> assertSame(pizzaMargherita, pedido.getItens().get(1)),
            () -> assertSame(refrigerante, pedido.getItens().get(2))
        );
    }

    @Test
    @DisplayName("UT-31: setters de número, dia da semana e status refletem nos getters")
    void settersCadastraisRefletemNosGetters() {
        Pedido pedido = new Pedido(1);

        pedido.setNumPedido(42);
        pedido.setDiaDaSemana(3);
        pedido.setStatus("SERVIDO");

        assertAll(
            () -> assertEquals(42, pedido.getNumPedido()),
            () -> assertEquals(3, pedido.getDiaSemana()),
            () -> assertEquals("SERVIDO", pedido.getStatus())
        );

        pedido.setStatus("CANCELADO");
        assertEquals("CANCELADO", pedido.getStatus());
    }

    @Test
    @DisplayName("UT-32: avaliação aceita as bordas do intervalo válido (1 e 5)")
    void avaliacaoAceitaAsBordasDoIntervalo() {
        Pedido pedido = new Pedido(1);

        pedido.setAvaliacao(1);
        assertEquals(1, pedido.getAvaliacao());

        pedido.setAvaliacao(5);
        assertEquals(5, pedido.getAvaliacao());
    }

    @Test
    @DisplayName("UT-33: motivo de cancelamento começa como texto vazio e nunca fica nulo")
    void motivoCancelamentoComecaVazioENuncaNulo() {
        Pedido pedido = new Pedido(1);

        assertNotNull(pedido.getMotivoCancelamento());
        assertEquals("", pedido.getMotivoCancelamento());

        pedido.setMotivoCancelamento("Cliente desistiu");
        assertEquals("Cliente desistiu", pedido.getMotivoCancelamento());
    }
}
