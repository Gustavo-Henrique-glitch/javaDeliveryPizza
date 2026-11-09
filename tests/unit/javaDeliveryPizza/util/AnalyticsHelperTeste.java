package javaDeliveryPizza.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javaDeliveryPizza.domain.Bebida;
import javaDeliveryPizza.domain.ItemProduto;
import javaDeliveryPizza.domain.Pedido;
import javaDeliveryPizza.domain.Pizza;

class AnalyticsHelperTeste {

    @Test
    @DisplayName("UT-01: valorTotal soma preço × quantidade de produtos diferentes")
    void valorTotalSomaItensDeTiposDiferentes() {
        Pedido pedido = new Pedido(1);
        pedido.addItem(new ItemProduto(new Pizza("Calabresa", "G", 40.0), 2));
        pedido.addItem(new ItemProduto(new Bebida("Cola", "Lata", 8.0), 3));

        assertEquals(104.0, AnalyticsHelper.valorTotal(pedido), 0.001);
    }
}
