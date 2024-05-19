package br.com.tech.challenge.mspagamento.core.usecase;

import br.com.tech.challenge.mspagamento.core.event.PagamentoRealizadoEvent;
import br.com.tech.challenge.mspagamento.core.event.publisher.PagamentoRealizadoEventPublisher;
import br.com.tech.challenge.mspagamento.core.exception.PagamentoInexistenteException;
import br.com.tech.challenge.mspagamento.core.exception.PagamentoJaRealizadoException;
import br.com.tech.challenge.mspagamento.core.gateway.PagamentoGateway;

public class RealizarPagamentoUseCase {
    private final PagamentoGateway pagamentoGateway;
    private final PagamentoRealizadoEventPublisher pagamentoRealizadoEventPublisher;

    public RealizarPagamentoUseCase(PagamentoGateway pagamentoGateway, PagamentoRealizadoEventPublisher pagamentoRealizadoEventPublisher) {
        this.pagamentoGateway = pagamentoGateway;
        this.pagamentoRealizadoEventPublisher = pagamentoRealizadoEventPublisher;
    }

    public void executar(Long idPedido) {
        var pagamento = pagamentoGateway.obterPagamentoPorIdPedido(idPedido)
                .orElseThrow(() -> new PagamentoInexistenteException(idPedido));

        if (Boolean.TRUE.equals(pagamento.estaPago())) {
            throw new PagamentoJaRealizadoException(idPedido);
        }

        pagamento.definirPago();

        pagamentoGateway.salvar(pagamento);

        pagamentoRealizadoEventPublisher.publicar(new PagamentoRealizadoEvent(pagamento));
    }
}
