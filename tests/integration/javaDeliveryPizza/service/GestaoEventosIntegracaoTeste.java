package javaDeliveryPizza.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Scanner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javaDeliveryPizza.domain.Evento;
import javaDeliveryPizza.helpers.SaidaConsole;

class GestaoEventosIntegracaoTeste {

    @Test
    @DisplayName("IT-14: atualizarEvento sem eventos cadastrados avisa e devolve false sem pedir dados")
    void atualizarSemEventosCadastrados() {
        ArrayList<Evento> eventos = new ArrayList<>();
        boolean[] atualizado = new boolean[1];
        String saida = SaidaConsole.capturar(() -> atualizado[0] = GestaoEventos.atualizarEvento(new Scanner(""), eventos));

        assertAll(
            () -> assertFalse(atualizado[0]),
            () -> assertTrue(eventos.isEmpty()),
            () -> assertTrue(saida.contains("Não há eventos cadastrados."))
        );
    }

    @Test
    @DisplayName("IT-15: atualizarEvento com id inexistente avisa e não mexe em nenhum evento")
    void atualizarEventoInexistente() {
        Evento existente = new Evento(1);
        existente.setNome("Seminário");
        existente.setCapacidade(100);
        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(existente);

        boolean[] atualizado = new boolean[1];
        String saida = SaidaConsole.capturar(() -> atualizado[0] = GestaoEventos.atualizarEvento(new Scanner("99"), eventos));

        assertAll(
            () -> assertFalse(atualizado[0]),
            () -> assertEquals(1, eventos.size()),
            () -> assertSame(existente, eventos.get(0)),
            () -> assertEquals("AGENDADO", existente.getStatus()),
            () -> assertTrue(saida.contains("Evento não encontrado.")),
            () -> assertFalse(saida.contains("Evento atualizado."))
        );
    }

    @Test
    @DisplayName("IT-16: marcar como REALIZADO grava público, ingressos vendidos e avaliação")
    void atualizarParaRealizado() {
        Evento evento = new Evento(1);
        evento.setCapacidade(100);
        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(evento);
        String entradas = String.join("\n", "1", "2", "80", "4");

        boolean[] atualizado = new boolean[1];
        String saida = SaidaConsole.capturar(() -> atualizado[0] = GestaoEventos.atualizarEvento(new Scanner(entradas), eventos));

        assertAll(
            () -> assertTrue(atualizado[0]),
            () -> assertEquals("REALIZADO", evento.getStatus()),
            () -> assertEquals(80, evento.getIngressosVendidos()),
            () -> assertEquals(80, evento.getPublicoReal()),
            () -> assertEquals(4, evento.getAvaliacao()),
            () -> assertTrue(saida.contains("1-AGENDADO  2-REALIZADO  3-CANCELADO")),
            () -> assertTrue(saida.contains("Status atual: AGENDADO")),
            () -> assertTrue(saida.contains("Evento atualizado."))
        );
    }

    @Test
    @DisplayName("IT-17: público acima da capacidade é limitado à capacidade do evento")
    void publicoAcimaDaCapacidadeEhLimitado() {
        Evento evento = new Evento(1);
        evento.setCapacidade(100);
        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(evento);

        SaidaConsole.capturar(() -> GestaoEventos.atualizarEvento(new Scanner(String.join("\n", "1", "2", "150", "5")), eventos));

        assertAll(
            () -> assertEquals("REALIZADO", evento.getStatus()),
            () -> assertEquals(100, evento.getIngressosVendidos()),
            () -> assertEquals(100, evento.getPublicoReal()),
            () -> assertEquals(5, evento.getAvaliacao())
        );
    }

    @Test
    @DisplayName("IT-18: marcar como CANCELADO zera ingressos, público e avaliação")
    void atualizarParaCancelado() {
        Evento evento = new Evento(1);
        evento.setCapacidade(100);
        evento.setStatus("REALIZADO");
        evento.setIngressosVendidos(70);
        evento.setAvaliacao(3);
        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(evento);

        SaidaConsole.capturar(() -> GestaoEventos.atualizarEvento(new Scanner(String.join("\n", "1", "3")), eventos));

        assertAll(
            () -> assertEquals("CANCELADO", evento.getStatus()),
            () -> assertEquals(0, evento.getIngressosVendidos()),
            () -> assertEquals(0, evento.getPublicoReal()),
            () -> assertEquals(0, evento.getAvaliacao())
        );
    }

    @Test
    @DisplayName("IT-19: voltar para AGENDADO apaga público, ingressos e avaliação de um evento já realizado")
    void atualizarParaAgendadoLimpaOsNumeros() {
        Evento evento = new Evento(1);
        evento.setCapacidade(100);
        evento.setStatus("REALIZADO");
        evento.setIngressosVendidos(60);
        evento.setAvaliacao(4);
        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(evento);

        String saida = SaidaConsole.capturar(() -> GestaoEventos.atualizarEvento(new Scanner(String.join("\n", "1", "1")), eventos));

        assertAll(
            () -> assertEquals("AGENDADO", evento.getStatus()),
            () -> assertEquals(0, evento.getIngressosVendidos()),
            () -> assertEquals(0, evento.getPublicoReal()),
            () -> assertEquals(0, evento.getAvaliacao()),
            () -> assertTrue(saida.contains("Status atual: REALIZADO")),
            () -> assertTrue(saida.contains("Evento atualizado."))
        );
    }

    @Test
    @DisplayName("IT-20: status fora de 1..3 é recusado sem alterar o evento")
    void statusInvalidoNaoAlteraOEvento() {
        Evento evento = new Evento(1);
        evento.setCapacidade(100);
        evento.setStatus("REALIZADO");
        evento.setIngressosVendidos(40);
        evento.setAvaliacao(3);
        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(evento);

        boolean[] atualizado = new boolean[1];
        String saida = SaidaConsole.capturar(() -> atualizado[0] = GestaoEventos.atualizarEvento(new Scanner(String.join("\n", "1", "9")), eventos));

        assertAll(
            () -> assertFalse(atualizado[0]),
            () -> assertEquals("REALIZADO", evento.getStatus()),
            () -> assertEquals(40, evento.getIngressosVendidos()),
            () -> assertEquals(40, evento.getPublicoReal()),
            () -> assertEquals(3, evento.getAvaliacao()),
            () -> assertTrue(saida.contains("Status inválido.")),
            () -> assertFalse(saida.contains("Evento atualizado."))
        );
    }

    @Test
    @DisplayName("IT-21: removerEvento retira apenas o evento do id informado e preserva os demais")
    void removerEventoRemoveApenasOAlvo() {
        Evento primeiro = new Evento(1);
        Evento alvo = new Evento(2);
        Evento ultimo = new Evento(3);
        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(primeiro);
        eventos.add(alvo);
        eventos.add(ultimo);

        boolean removido = GestaoEventos.removerEvento(eventos, 2);

        assertAll(
            () -> assertTrue(removido),
            () -> assertEquals(2, eventos.size()),
            () -> assertSame(primeiro, eventos.get(0)),
            () -> assertSame(ultimo, eventos.get(1))
        );
    }

    @Test
    @DisplayName("IT-22: removerEvento devolve false e mantém a lista quando o id não existe")
    void removerEventoInexistenteNaoAlteraALista() {
        Evento primeiro = new Evento(1);
        Evento segundo = new Evento(2);
        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(primeiro);
        eventos.add(segundo);

        boolean removido = GestaoEventos.removerEvento(eventos, 99);

        assertAll(
            () -> assertFalse(removido),
            () -> assertEquals(2, eventos.size()),
            () -> assertSame(primeiro, eventos.get(0)),
            () -> assertSame(segundo, eventos.get(1))
        );
    }
}
