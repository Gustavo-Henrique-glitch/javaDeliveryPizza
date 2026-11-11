package javaDeliveryPizza.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventoTeste {

    @Test
    @DisplayName("UT-04: setAvaliacao mantém valores válidos e limita os demais a 0..5")
    void setAvaliacaoLimitaValoresAFaixaValida() {
        Evento evento = new Evento(1);

        evento.setAvaliacao(3);
        assertEquals(3, evento.getAvaliacao());

        evento.setAvaliacao(-1);
        assertEquals(0, evento.getAvaliacao());

        evento.setAvaliacao(6);
        assertEquals(5, evento.getAvaliacao());
    }

    @Test
    @DisplayName("UT-02: Um evento  recem-criado começa como Agendado e  com valos zerados ")
    void eventoRecemCriadoComecaAgendadoComValoresZerados(){
        Evento evento= new Evento(1);
        assertAll(
            ()-> assertEquals(1, evento.getId()),
            ()-> assertEquals("AGENDADO",evento.getStatus()),
            ()-> assertEquals(0, evento.getBuffet().size()),
            ()-> assertEquals(0, evento.getPublicoReal()),
            ()-> assertEquals(0, evento.getIngressosVendidos()),
            ()-> assertEquals(0, evento.getAvaliacao()),
            ()-> assertEquals(0, evento.getPrecoIngresso(), 0.001)

        );
    }
}
