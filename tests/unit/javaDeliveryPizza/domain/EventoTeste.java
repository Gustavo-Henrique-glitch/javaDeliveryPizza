package javaDeliveryPizza.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
