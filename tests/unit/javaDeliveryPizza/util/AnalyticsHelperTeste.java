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
import java.util.ArrayList;
import java.util.List;

import javaDeliveryPizza.domain.Evento;
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
    @DisplayName("UT-10: nomeDia retorna abreviação correta para cada dia da semana")
    void traduzNumerosDoDia(int dia, String eperado){
        assertEquals(eperado, AnalyticsHelper.nomeDia(dia));
    }


    @Test 
    @DisplayName ("UT- 11: arred2 arredonda para duas casas decimais")
    void arredondarParaDuasCasasDecimais(){
        
        assertEquals(3.14, AnalyticsHelper.arred2(3.14159), 0.001);
        assertEquals(2.72, AnalyticsHelper.arred2(2.71828), 0.001);
        assertEquals(1.41, AnalyticsHelper.arred2(1.41421), 0.001);
    }

    @Test 
    @DisplayName ("UT- 12: encontra a posição da chave na lista")
    void encontrarPosicaoDaChaveNaLista(){
        ArrayList<String> lista = new ArrayList<>(List.of("ana", "gustavo", "joao"));
        assertEquals(1, AnalyticsHelper.indice(lista, "gustavo"));
        assertEquals(-1, AnalyticsHelper.indice(lista, "maria"));
        assertEquals(0, AnalyticsHelper.indice(lista, "ana"));
        assertEquals(2, AnalyticsHelper.indice(lista, "joao"));
    }

    @Test 
    @DisplayName("UT- 13: retorna o indice do maior elemento da lista")
    void retornaIdiceDoMaiorElementoDaLista(){
        assertEquals(1, AnalyticsHelper.indiceMaior(new ArrayList<>(List.of(1,5,2))));
        assertEquals(0, AnalyticsHelper.indiceMaior(new ArrayList<>(List.of(5,5,2))));
        assertEquals(0, AnalyticsHelper.indiceMaior(new ArrayList<>()));

    }

    @Test 
    @DisplayName ("UT -14: valor total é zero quando não há itens no pedido")
    void valorTotalEhZeroQuandoNaoHaItensNoPedido(){
        Pedido pedido = new Pedido(1);
        assertEquals(0.0, AnalyticsHelper.valorTotal(pedido), 0.001);
    }

    @Test 
    @DisplayName ("UT -15: totalBuffet calcula corretamente o valor total do buffet")
    void totalBuffetCalculaCorretamente(){
        Evento evento = new Evento(1);
        evento.adicionarItemBuffet(new ItemProduto(new Pizza("Calabresa", "G", 40.0), 2));
        evento.adicionarItemBuffet(new ItemProduto(new Bebida("Cola", "Lata", 8.0), 3));
        assertEquals(104.0, AnalyticsHelper.totalBuffet(evento), 0.001);
    }
}
