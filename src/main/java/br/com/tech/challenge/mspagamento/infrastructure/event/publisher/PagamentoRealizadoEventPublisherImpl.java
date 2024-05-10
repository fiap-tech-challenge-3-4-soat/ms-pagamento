package br.com.tech.challenge.mspagamento.infrastructure.event.publisher;

import br.com.tech.challenge.mspagamento.core.event.PagamentoRealizadoEvent;
import br.com.tech.challenge.mspagamento.core.event.publisher.PagamentoRealizadoEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagamentoRealizadoEventPublisherImpl implements PagamentoRealizadoEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publicar(PagamentoRealizadoEvent event) {
        eventPublisher.publishEvent(event);
    }
}
