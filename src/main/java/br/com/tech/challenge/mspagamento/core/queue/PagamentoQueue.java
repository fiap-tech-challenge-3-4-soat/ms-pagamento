package br.com.tech.challenge.mspagamento.core.queue;

import br.com.tech.challenge.mspagamento.core.event.PagamentoRealizadoEvent;

public interface PagamentoQueue {
    void publicarPagamentoRealizado(PagamentoRealizadoEvent pagamentoRealizadoEvent);
}
