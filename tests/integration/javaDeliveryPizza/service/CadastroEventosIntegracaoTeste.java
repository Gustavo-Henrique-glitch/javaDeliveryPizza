package javaDeliveryPizza.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javaDeliveryPizza.domain.Bebida;
import javaDeliveryPizza.domain.Evento;
import javaDeliveryPizza.domain.ItemProduto;
import javaDeliveryPizza.domain.Pizza;
import javaDeliveryPizza.helpers.SaidaConsole;

class CadastroEventosIntegracaoTeste {

    @BeforeEach
    void reiniciarIds() {
        CadastroEventos.configurarProximoId(1);
    }

    @Test
    @DisplayName("IT-10: evento digitado sem buffet chega completo à lista, já AGENDADO e com o id anunciado")
    void cadastrarEventoSemBuffet() {
        String entradas = String.join("\n","Workshop de Java","workshop","3","50","120.00","0");                
        ArrayList<Evento> eventos = new ArrayList<>();

        String saida = SaidaConsole.capturar(() -> CadastroEventos.cadastrarEvento(new Scanner(entradas), eventos));

        Evento evento = eventos.get(0);
        assertAll(
            () -> assertEquals(1, eventos.size()),
            () -> assertEquals(1, evento.getId()),
            () -> assertEquals("Workshop de Java", evento.getNome()),
            () -> assertEquals("workshop", evento.getTipo()),
            () -> assertEquals(3, evento.getDiaSemana()),
            () -> assertEquals(50, evento.getCapacidade()),
            () -> assertEquals(120.0, evento.getPrecoIngresso(), 0.001),
            () -> assertEquals("AGENDADO", evento.getStatus()),
            () -> assertEquals(0, evento.getPublicoReal()),
            () -> assertEquals(0, evento.getIngressosVendidos()),
            () -> assertTrue(evento.getBuffet().isEmpty()),
            () -> assertTrue(saida.contains("Evento #1 cadastrado com sucesso."))
        );
    }

    @Test
    @DisplayName("IT-11: buffet digitado vira pizza e bebida na ordem em que foram informadas")
    void cadastrarEventoComPizzaEBebidaNoBuffet() {
        String entradas = String.join("\n","Festa Junina","show","6","200","45.50","1","1","Calabresa","40.00","G","2","1","2","Suco","8.50","1L","3","0");           
        ArrayList<Evento> eventos = new ArrayList<>();

        SaidaConsole.capturar(() -> CadastroEventos.cadastrarEvento(new Scanner(entradas), eventos));

        Evento evento = eventos.get(0);
        ItemProduto primeiroItem = evento.getBuffet().get(0);
        ItemProduto segundoItem = evento.getBuffet().get(1);
        Pizza pizza = assertInstanceOf(Pizza.class, primeiroItem.getProduto());
        Bebida bebida = assertInstanceOf(Bebida.class, segundoItem.getProduto());

        assertAll(
            () -> assertEquals(2, evento.getBuffet().size()),
            () -> assertEquals("Calabresa", pizza.getSabor()),
            () -> assertEquals("G", pizza.getTamanho()),
            () -> assertEquals(40.0, pizza.getPreco(), 0.001),
            () -> assertEquals(2, primeiroItem.getQuantidade()),
            () -> assertEquals("Suco", bebida.getNome()),
            () -> assertEquals("1L", bebida.getTamanho()),
            () -> assertEquals(8.5, bebida.getPreco(), 0.001),
            () -> assertEquals(3, segundoItem.getQuantidade())
        );
    }

    @Test
    @DisplayName("IT-12: item de buffet com tipo inválido é recusado e não impede o próximo item")
    void itemDeBuffetInvalidoNaoEntraNaLista() {
        String entradas = String.join("\n","Palestra DevOps","palestra","2","80","30.00","1","9","1","2","Água","5.00","Lata","4","0");
        ArrayList<Evento> eventos = new ArrayList<>();

        String saida = SaidaConsole.capturar(() -> CadastroEventos.cadastrarEvento(new Scanner(entradas), eventos));

        Evento evento = eventos.get(0);
        ItemProduto unicoItem = evento.getBuffet().get(0);

        assertAll(
            () -> assertEquals(1, evento.getBuffet().size()),
            () -> assertEquals("Água", ((Bebida) unicoItem.getProduto()).getNome()),
            () -> assertEquals(4, unicoItem.getQuantidade()),
            () -> assertTrue(saida.contains("Opção inválida."))
        );
    }

    @Test
    @DisplayName("IT-13: cada evento cadastrado consome um id, a partir do número configurado")
    void eventosSeguidosRecebemIdsSequenciais() {
        String umEvento = String.join("\n", "Meetup", "meetup", "4", "30", "10.00", "0");
        ArrayList<Evento> eventos = new ArrayList<>();
        CadastroEventos.configurarProximoId(7);

        SaidaConsole.capturar(() -> {
            CadastroEventos.cadastrarEvento(new Scanner(umEvento), eventos);
            CadastroEventos.cadastrarEvento(new Scanner(umEvento), eventos);
        });

        assertAll(
            () -> assertEquals(2, eventos.size()),
            () -> assertEquals(7, eventos.get(0).getId()),
            () -> assertEquals(8, eventos.get(1).getId()),
            () -> assertNotSame(eventos.get(0), eventos.get(1))
        );
    }
}
