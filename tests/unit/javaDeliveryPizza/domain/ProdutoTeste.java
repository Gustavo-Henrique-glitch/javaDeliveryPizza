package javaDeliveryPizza.domain;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
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
}
