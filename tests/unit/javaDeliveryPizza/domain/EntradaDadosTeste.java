package javaDeliveryPizza.domain;
import javaDeliveryPizza.util.EntradaDados; 
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;
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

    @Test
    @DisplayName("UT-10: lerDouble rejeita texto, avisa 'Valor inválido' e lê a próxima linha válida")
    void lerDoubleRepeteAposEntradaInvalida() {
        Scanner sc = new Scanner("abc\n3.5\n");
        double[] lido = new double[1];

        String saida = SaidaConsole.capturar(() -> lido[0] = EntradaDados.lerDouble(sc));

        assertAll(
            () -> assertEquals(3.5, lido[0], 0.001),
            () -> assertTrue(saida.contains("Valor inválido"))
        );
    }

    @Test
    @DisplayName("UT-11: lerLinha devolve a linha crua, sem trim nem conversão")
    void lerLinhaDevolveLinhaCrua() {
        Scanner sc = new Scanner("  abc def  \n");

        assertEquals("  abc def  ", EntradaDados.lerLinha(sc));
    }

    @Test
    @DisplayName("UT-12: lerInteiro rejeita linha vazia e linha só com espaços")
    void lerInteiroRejeitaLinhaVaziaEEspacos() {
        Scanner sc = new Scanner("\n   \n9\n");
        int[] lido = new int[1];

        String saida = SaidaConsole.capturar(() -> lido[0] = EntradaDados.lerInteiro(sc));

        assertAll(
            () -> assertEquals(9, lido[0]),
            () -> assertEquals(2, contarOcorrencias(saida, "Valor inválido"))
        );
    }

    @Test
    @DisplayName("UT-13: lerInteiro rejeita decimal (3.5) e lê a próxima linha válida")
    void lerInteiroRejeitaDecimal() {
        Scanner sc = new Scanner("3.5\n7\n");
        int[] lido = new int[1];

        String saida = SaidaConsole.capturar(() -> lido[0] = EntradaDados.lerInteiro(sc));

        assertAll(
            () -> assertEquals(7, lido[0]),
            () -> assertTrue(saida.contains("Valor inválido"))
        );
    }

    @Test
    @DisplayName("UT-14: lerInteiro e lerDouble aceitam zero e negativos (sem validação de faixa)")
    void lerAceitaZeroENegativos() {
        Scanner inteiroSc = new Scanner("0\n");
        Scanner negativoSc = new Scanner("-42\n");
        Scanner decimalSc = new Scanner("-7.5\n");

        assertAll(
            () -> assertEquals(0, EntradaDados.lerInteiro(inteiroSc)),
            () -> assertEquals(-42, EntradaDados.lerInteiro(negativoSc)),
            () -> assertEquals(-7.5, EntradaDados.lerDouble(decimalSc), 0.001)
        );
    }

    @Test
    @DisplayName("UT-15: lerDouble rejeita 3,5 (dependência de locale) e lê 2.5")
    void lerDoubleRejeitaSeparadorDecimalComVirgula() {
        Scanner sc = new Scanner("3,5\n2.5\n");
        double[] lido = new double[1];

        String saida = SaidaConsole.capturar(() -> lido[0] = EntradaDados.lerDouble(sc));

        assertAll(
            () -> assertEquals(2.5, lido[0], 0.001),
            () -> assertTrue(saida.contains("Valor inválido"))
        );
    }

    @Test
    @DisplayName("UT-16: lerInteiro rejeita overflow (99999999999) e lê a próxima linha válida")
    void lerInteiroRejeitaOverflow() {
        Scanner sc = new Scanner("99999999999\n5\n");
        int[] lido = new int[1];

        String saida = SaidaConsole.capturar(() -> lido[0] = EntradaDados.lerInteiro(sc));

        assertAll(
            () -> assertEquals(5, lido[0]),
            () -> assertTrue(saida.contains("Valor inválido"))
        );
    }

    @Test
    @DisplayName("UT-17: entrada esgotada (EOF) lança NoSuchElementException nas três leituras")
    void leituraComEntradaEsgotadaLancaNoSuchElementException() {
        assertAll(
            () -> assertThrows(NoSuchElementException.class, () -> EntradaDados.lerInteiro(new Scanner(""))),
            () -> assertThrows(NoSuchElementException.class, () -> EntradaDados.lerDouble(new Scanner(""))),
            () -> assertThrows(NoSuchElementException.class, () -> EntradaDados.lerLinha(new Scanner("")))
        );
    }

    private static int contarOcorrencias(String texto, String trecho) {
        int total = 0;
        int posicao = texto.indexOf(trecho);
        while (posicao >= 0) {
            total++;
            posicao = texto.indexOf(trecho, posicao + trecho.length());
        }
        return total;
    }
}
