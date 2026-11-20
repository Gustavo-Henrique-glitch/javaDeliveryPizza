# Defeitos conhecidos — javaDeliveryPizza

Registro dos comportamentos que **contrariam a regra de negócio** e que os testes
documentam. Este arquivo existe para que um teste verde de caracterização não seja
confundido com aprovação do comportamento.


## F-01 — Faturamento consolidado inclui eventos não realizados

- **Local:** `src/javaDeliveryPizza/service/Relatorios.java`, `faturamentoTotal` (Pergunta 9).
- **Tipo:** ativo. **Severidade:** alta.
- **Sintoma:** a receita de eventos soma ingressos + buffet de **todos** os eventos da
  lista, sem filtrar por status. Eventos `CANCELADO` e `AGENDADO` entram no total.

## F-02 — Capacidade de evento aceita valor negativo

- **Local:** `src/javaDeliveryPizza/domain/Evento.java:68` (`setCapacidade`), alimentado
  por `CadastroEventos.java:38` (`lerInteiro` sem validação de faixa).
- **Tipo:** latente. **Severidade:** média.
- **Sintoma:** `setCapacidade(-50)` é aceito e armazenado. Todos os outros setters de
  `Evento` validam (`setPublicoReal`, `setIngressosVendidos`, `setPrecoIngresso`,
  `setAvaliacao`); este não valida nada.


## F-03 — `setIngressosVendidos` sobrescreve o público real

- **Local:** `src/javaDeliveryPizza/domain/Evento.java:87`, linha
  `this.publicoReal = ingressosVendidos;`.
- **Tipo:** latente. **Severidade:** média.
- **Sintoma:** um setter altera dois campos. Além disso, `setIngressosVendidos(0)` apaga
  o público real já registrado.
