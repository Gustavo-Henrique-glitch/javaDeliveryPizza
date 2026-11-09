package javaDeliveryPizza.regression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Executa o programa pelo terminal, porque os pacotes mudam entre os commits.
 * As propriedades indicam as classes e a classe principal da versão testada.
 */
class RegressaoTeste {
    private static final String CLASSES = System.getProperty("sistema.classes");
    private static final String PRINCIPAL = System.getProperty("sistema.principal");

    private static final Pattern EVENTO_CADASTRADO = Pattern.compile("Evento #(\\d+) cadastrado");
    private static final String LINHA_FATURAMENTO = "Faturamento consolidado:";

    private static final String[] MENU_FUNCIONARIO = {"2"};
    private static final String[] VOLTAR = {"0"};
    private static final String[] SAIR = {"0"};
    private static final String[] VER_RELATORIO = {"1", "123oi", "1", "0"};
    private static final String[] VER_VENDAS = {"2"};
    private static final String[] REGISTRAR_VENDA = {
        "1", "1", "1", "5", "1", "SaborRegressao", "40", "G", "2", "0"
    };
    private static final String[] CADASTRAR_EVENTO_COM_BUFFET = {
        "3", "Evento Regressao", "Show", "3", "100", "50",
        "1", "1", "Calabresa", "1000", "G", "1", "0"
    };

    @Test
    @DisplayName("RG-01: venda registrada aparece na listagem com o total correto")
    void vendaRegistradaApareceNaListagem() throws Exception {
        String saida = executar(MENU_FUNCIONARIO, REGISTRAR_VENDA, VER_VENDAS, VOLTAR, SAIR);

        List<String> linhas = saida.lines().toList();
        int item = indiceDaLinhaCom(linhas, "Pizza SaborRegressao (G) x2");

        assertTrue(item > 0, "item da venda não encontrado na listagem");
        assertTrue(linhas.get(item - 1).contains("Total=R$ 80.0"), linhas.get(item - 1));
    }

    @Test
    @DisplayName("RG-02: evento cancelado não altera o faturamento consolidado (F-01)")
    void eventoCanceladoNaoAlteraFaturamento() throws Exception {
        String faturamentoInicial = linhaDeFaturamento(executar(VER_RELATORIO, SAIR));

        String[] cancelarEvento = {"5", idDoEventoCadastrado(), "3"};
        String saida = executar(MENU_FUNCIONARIO, CADASTRAR_EVENTO_COM_BUFFET, cancelarEvento,
                VOLTAR, VER_RELATORIO, SAIR);

        assertEquals(faturamentoInicial, linhaDeFaturamento(saida));
    }

    private static String idDoEventoCadastrado() throws Exception {
        String saida = executar(MENU_FUNCIONARIO, CADASTRAR_EVENTO_COM_BUFFET, VOLTAR, SAIR);
        Matcher encontrado = EVENTO_CADASTRADO.matcher(saida);
        assertTrue(encontrado.find(), "evento não foi cadastrado");
        return encontrado.group(1);
    }

    private static int indiceDaLinhaCom(List<String> linhas, String trecho) {
        for (int i = 0; i < linhas.size(); i++) {
            if (linhas.get(i).contains(trecho)) {
                return i;
            }
        }
        return -1;
    }

    private static String linhaDeFaturamento(String saida) {
        return saida.lines()
                .filter(linha -> linha.startsWith(LINHA_FATURAMENTO))
                .findFirst()
                .orElse("(linha de faturamento ausente)");
    }

    private static String executar(String[]... passos) throws IOException, InterruptedException {
        StringBuilder entrada = new StringBuilder();
        for (String[] passo : passos) {
            for (String linha : passo) {
                entrada.append(linha).append('\n');
            }
        }

        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        Process processo = new ProcessBuilder(java, "-cp", CLASSES, PRINCIPAL)
                .redirectErrorStream(true)
                .start();
        try (OutputStream teclado = processo.getOutputStream()) {
            teclado.write(entrada.toString().getBytes(StandardCharsets.UTF_8));
        }
        String saida = new String(processo.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(0, processo.waitFor(), saida);
        return saida;
    }
}
