package javaDeliveryPizza.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javaDeliveryPizza.domain.Bebida;
import javaDeliveryPizza.domain.ItemProduto;
import javaDeliveryPizza.domain.Pedido;
import javaDeliveryPizza.domain.Pizza;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AnalyticsHelperTeste {

    @Test
    @DisplayName("UT-01: valorTotal soma preço × quantidade de produtos diferentes")
    void valorTotalSomaItensDeTiposDiferentes() {
        Pedido pedido = new Pedido(1);
        pedido.addItem(new ItemProduto(new Pizza("Calabresa", "G", 40.0), 2));
        pedido.addItem(new ItemProduto(new Bebida("Cola", "Lata", 8.0), 3));

        assertEquals(104.0, AnalyticsHelper.valorTotal(pedido), 0.001);
    }

    @ParameterizedTest (name = "dia{0}-> {1} ")
    @CsvSource({
        "1,Seg",
        "2,Ter",
        "3,Qua",
        "4,Qui",
        "5,Sex",
        "6,Sab",
        "7,Dom",
        "8,?"
    })
    @DisplayName("UT-00: nomeDia retorna abreviação correta para cada dia da semana")
    void traduzNumerosDoDia(int dia, String eperado){
        assertEquals(eperado, AnalyticsHelper.nomeDia(dia));
    }
    
}
