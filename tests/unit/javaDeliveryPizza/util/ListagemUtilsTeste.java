package javaDeliveryPizza.util;

import javaDeliveryPizza.util.ListagemUtils;
import javaDeliveryPizza.domain.Bebida;
import javaDeliveryPizza.domain.ItemProduto;
import javaDeliveryPizza.domain.Produto;
import javaDeliveryPizza.domain.Pizza;
import javaDeliveryPizza.domain.Bebida;
import javaDeliveryPizza.domain.ItemProduto;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;
import java.util.Scanner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ListagemUtilsTeste {

    @Test
    @DisplayName("UT-?: nomeProduto completo")
    public void nomeDoProdutoCompletoComTamanho() {
        Pizza pi = new Pizza("Calabresa", "G", 30);
        Bebida be = new Bebida("Coca Cola", "Lata", 1);

        ItemProduto pp = new ItemProduto(pi, 0);
        ItemProduto pb = new ItemProduto(be, 0);

        String npp = pp.getProduto().nomeBase();
        String tpp = pp.getProduto().getTamanho();

        String npb = pb.getProduto().nomeBase();
        String tpb = pb.getProduto().getTamanho();

        assertEquals(npp + " (" + tpp + ")", ListagemUtils.nomeProduto(pi));
        assertEquals(npb + " (" + tpb + ")", ListagemUtils.nomeProduto(be));

    }

    @Test
    @DisplayName("UT-?: nomeProduto completo")
    public void produtoNulo() {
        String resposta = ListagemUtils.nomeProduto(null); 
        assertEquals("Produto", resposta);
    }

}
