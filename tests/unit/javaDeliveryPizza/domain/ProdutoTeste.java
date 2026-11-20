package javaDeliveryPizza.domain;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.DisplayName;
import org.junit.Test;

public class ProdutoTeste {
    
    @Test 
    @DisplayName("UT-25: Criar uma pizza com sabor, tamanho e preço válidos e verificar se os atributos são atribuídos corretamente")
    public void criarPizzaComAtributosValidos() {
        Pizza pizza = new Pizza("Calabresa", "G", 25.0);
        assertAll(
            ()-> assertEquals("Calabresa", pizza.getSabor()),
            ()-> assertEquals("G", pizza.getTamanho()),
            ()-> assertEquals(25.0, pizza.getPreco(), 0.001),
            ()-> assertEquals("Pizza Calabresa", pizza.nomeBase()),
            ()-> assertEquals("Pizza Calabresa (G)", pizza.descricao())
        );
        pizza.setSabor("Mussarela");
        assertEquals("Mussarela", pizza.getSabor());
    }

    @Test 
    @DisplayName("UT- 26: Criar uma bebida com tamanho e preço válidos e verificar se os atributos são atribuídos corretamente")
    public void criarBebidaComAtributosValidos() {
        Bebida bebida = new Bebida("Coca-Cola", "G", 10.0);
        assertAll(
            ()-> assertEquals("Coca-Cola", bebida.getNome()),
            ()-> assertEquals("G", bebida.getTamanho()),
            ()-> assertEquals(10.0, bebida.getPreco(), 0.001),
            ()-> assertEquals("Bebida Coca-Cola", bebida.nomeBase()),
            ()-> assertEquals("Bebida Coca-Cola (G)", bebida.descricao())
        );
        bebida.setNome("Pepsi");
        assertEquals("Pepsi", bebida.getNome());
    
    }

    @Test 
    @DisplayName ("UT-27: Alterar o preço  e tamanho de uma pizza e verificar se o preço é atualizado corretamente")
    public void alterarPrecoETamanhoDeUmaPizza() {
        Pizza pizza = new Pizza("Calabresa", "G", 25.0);
        pizza.setPreco(30.0);
        pizza.setTamanho("M");
        assertAll(
            ()-> assertEquals(30.0, pizza.getPreco(), 0.001),
            ()-> assertEquals("M", pizza.getTamanho())
        );
    }

    @Test 
    @DisplayName ("UT-28: Criar itemProduto com pizza e quantidade e verificar se os atributos são atribuídos corretamente")
    public void criarItemProdutoComPizzaEQuantidade() {
        Pizza pizza = new Pizza("Calabresa", "G", 25.0);
        ItemProduto item = new ItemProduto(pizza, 10);
        assertAll(
           ()-> assertSame(pizza, item.getProduto()),
           ()-> assertEquals(10, item.getQuantidade())
        );
        Bebida bebida = new Bebida("Coca-Cola", "G", 10.0);
        item.setProduto(bebida);
        item.setQuantidade(5);
        assertAll(
            ()-> assertSame(bebida, item.getProduto()),
            ()-> assertEquals(5, item.getQuantidade())
        );
    }

}
