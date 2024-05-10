package br.com.tech.challenge.mspagamento.core.event.publisher;

import br.com.tech.challenge.mspagamento.core.event.PagamentoRealizadoEvent;

public interface PagamentoRealizadoEventPublisher {
    void publicar(PagamentoRealizadoEvent event);
}
