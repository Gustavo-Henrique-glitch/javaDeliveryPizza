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
    @DisplayName("UT-02: Um evento  recem-criado começa como Agendado e  com valores zerados")
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

    @Test
    @DisplayName("UT-03a [CARACTERIZAÇÃO - F-01]: setIngressosVendidos sobrescreve o valor em publicoReal (supeita de defeito)")
    void setIngressosVendidosEspelhaNoPublicoReal(){
        Evento  evento = new Evento(1);
        evento.setPublicoReal(10);
        evento.setIngressosVendidos(20);

        assertAll(

            ()-> assertEquals(20, evento.getIngressosVendidos()),
            ()-> assertEquals(20, evento.getPublicoReal())

        );
    }

    @Test
    @DisplayName("UT-03b: setPublicoReal não altera ingressosVendidos")
    void setPublicoRealNaoAlteraIngressosVendidos(){
        Evento evento = new Evento(1);
        evento.setIngressosVendidos(10);
        evento.setPublicoReal(20);

        assertAll(
            ()-> assertEquals(10, evento.getIngressosVendidos()),
            ()-> assertEquals(20, evento.getPublicoReal())
        );
    }

    @Test
    @DisplayName("UT-05 [CARACTERIZAÇÃO - F-02]: setIngressosVendidos limita valores negativos a zero e apagao o  publicoReal (suspeita de defeito)")
    void setIngressosVendidosLimitaValoresNegativos(){
        Evento evento = new Evento(1);
        evento.setPublicoReal(10);
        evento.setIngressosVendidos(-10);

        assertAll(
            ()-> assertEquals(0, evento.getIngressosVendidos()),
            ()-> assertEquals(0, evento.getPublicoReal())
        );


    }

    @Test
    @DisplayName("UT-06: setPublicoReal limita valores negativos sem alterar IngressosVendidos")
    void setPublicoRealLimitaValoresNegativos(){
        Evento evento = new Evento(1);
        evento.setIngressosVendidos(10);
        evento.setPublicoReal(-10);
        

        assertAll(
            ()-> assertEquals(10, evento.getIngressosVendidos()),
            ()-> assertEquals(0, evento.getPublicoReal())
        );


    }


    @Test
    @DisplayName ("UT-23: setPrecoIngresso limita valores negativos a zero")
    void setPrecoIngressoLimitaValoresNegativos(){
        Evento evento = new Evento(1);
        evento.setPrecoIngresso(50.0);
        evento.setPrecoIngresso(-10);
        assertEquals(0.0,evento.getPrecoIngresso(),0.001);
    }

    @Test 
    @DisplayName ("UT-08a: receita ingresso multiplica preco por ingressos vendidos")
    void receitaIngressoMultiplicaPrecoPorIngressosVendidos(){
        Evento evento = new Evento(1);
        evento.setPrecoIngresso(50.0);
        evento.setIngressosVendidos(10);
        assertEquals(500.0, evento.receitaIngressos(),0.001);
    }

    @Test 
    @DisplayName("UT-08b: receita ingresso retorna zero se preco for zero")
    void receitaIngressoEhZeroComPrecoZero(){
        Evento evento = new Evento(1);
        evento.setPrecoIngresso(0.0);
        evento.setIngressosVendidos(10);
        assertEquals(0.0, evento.receitaIngressos(),0.001);
    }

    @Test 
    @DisplayName("UT-08c: receita ingresso retorna zero se ingressos vendidos for zero")
    void receitaIngressoEhZeroSemIngressosVendidos(){
        Evento evento = new Evento(1);
        evento.setPrecoIngresso(50.0);
        evento.setIngressosVendidos(0);
        assertEquals(0.0, evento.receitaIngressos(),0.001);

    }

    @Test 
    @DisplayName ("UT-09: adicionarItemBuffet adiciona item ao buffet na ordem de inserção")
    void adicionarItemBuffetAdicionaItemAoBuffetNaOrdemDeInsercao(){
        Evento evento = new Evento(1);
        Pizza produto1 = new Pizza("calabresa","Grande",100);
        Pizza produto2 = new Pizza("margherita","Pequeno",50);
        ItemProduto item1 = new ItemProduto(produto1, 10);
        ItemProduto item2 = new ItemProduto(produto2, 5);
        evento.adicionarItemBuffet(item1);
        evento.adicionarItemBuffet(item2);

        assertAll(
            ()-> assertEquals(2, evento.getBuffet().size()),
            ()-> assertEquals(item1.getProduto().getTamanho(), evento.getBuffet().get(0).getProduto().getTamanho()),
            ()-> assertEquals(item1.getProduto().getPreco(), evento.getBuffet().get(0).getProduto().getPreco()),
            ()-> assertEquals(item2.getProduto().getTamanho(), evento.getBuffet().get(1).getProduto().getTamanho()),
            ()-> assertEquals(item2.getProduto().getPreco(), evento.getBuffet().get(1).getProduto().getPreco())
        );
    }


    @Test 
    @DisplayName("UT- 24 [CARACTERIZAÇÃO - F-03]: setCapacidade aceita valores negativos e não altera a capacidade (suspeita de defeito)")
    void setCapacidadeNaoAlteraCapacidadeComValoresNegativos(){
        Evento evento = new Evento(1);
        assertAll(
            ()-> assertEquals(0, evento.getCapacidade()),
            ()-> evento.setCapacidade(100),
            ()-> assertEquals(100, evento.getCapacidade()),
            ()-> evento.setCapacidade(-10),
            ()-> assertEquals(-10, evento.getCapacidade())
        );
        evento.setCapacidade(100);
        evento.setCapacidade(-10);
        
    }
}
