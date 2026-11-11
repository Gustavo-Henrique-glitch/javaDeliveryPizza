package javaDeliveryPizza.domain;
import javaDeliveryPizza.util.EntradaDados; 
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Scanner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javaDeliveryPizza.helpers.SaidaConsole;

class EntradaDadosTeste {

    @Test
    @DisplayName("UT-07: lerInteiro e lerDouble convertem valores validos ignorando espaços nas pontas")
    void lerConverteValoresValidosIgnorandoEspacos() {
        Scanner sc = new Scanner("  42  \n 3.5 \n");

        assertAll(
            () -> assertEquals(42, EntradaDados.lerInteiro(sc)),
            () -> assertEquals(3.5, EntradaDados.lerDouble(sc), 0.001)
        );
    }

    @Test
    @DisplayName("UT-08: lerInteiro rejeita texto invalido, avisa 'Valor invalido' e le a proxima linha")
    void lerInteiroRepeteAposEntradaInvalida() {
        Scanner sc = new Scanner("abc\n7\n");
        int[] lido = new int[1];

        String saida = SaidaConsole.capturar(() -> lido[0] = EntradaDados.lerInteiro(sc));

        assertAll(
            () -> assertEquals(7, lido[0]),
            () -> assertTrue(saida.contains("Valor inválido"))
        );
    }
}
