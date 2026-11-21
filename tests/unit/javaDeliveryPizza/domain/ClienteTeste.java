package javaDeliveryPizza.domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ClienteTeste {

    @Test
    @DisplayName("UT-34: cliente recém-criado expõe nome e distância informados no construtor")
    void clienteRecemCriadoGuardaNomeEDistancia() {
        Cliente cliente = new Cliente("Ana", 4.5);

        assertAll(
            () -> assertEquals("Ana", cliente.getNome()),
            () -> assertEquals(4.5, cliente.getDistancia(), 0.001)
        );
    }

    @Test
    @DisplayName("UT-35: setters atualizam os dados e exibirDados monta o texto com os valores atuais")
    void settersAtualizamOtextoDeExibicao() {
        Cliente cliente = new Cliente("Ana", 4.5);

        assertEquals("Nome: Ana, Distancia: 4.5km", cliente.exibirDados());

        cliente.setNome("Bruno");
        cliente.setDistancia(12.75);

        assertAll(
            () -> assertEquals("Bruno", cliente.getNome()),
            () -> assertEquals(12.75, cliente.getDistancia(), 0.001),
            () -> assertEquals("Nome: Bruno, Distancia: 12.75km", cliente.exibirDados())
        );
    }
}
