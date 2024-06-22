package br.com.tech.challenge.mspagamento.core.usecase;

import br.com.tech.challenge.mspagamento.core.event.PagamentoRealizadoEvent;
import br.com.tech.challenge.mspagamento.core.exception.PagamentoInexistenteException;
import br.com.tech.challenge.mspagamento.core.exception.PagamentoJaRealizadoException;
import br.com.tech.challenge.mspagamento.core.gateway.PagamentoGateway;
import br.com.tech.challenge.mspagamento.core.queue.PagamentoQueue;

public class RealizarPagamentoUseCase {
    private final PagamentoGateway pagamentoGateway;
    private final PagamentoQueue pagamentoQueue;

    public RealizarPagamentoUseCase(PagamentoGateway pagamentoGateway, PagamentoQueue pagamentoQueue) {
        this.pagamentoGateway = pagamentoGateway;
        this.pagamentoQueue = pagamentoQueue;
    }

    public void executar(Long idPedido) {
        var pagamento = pagamentoGateway.obterPagamentoPorIdPedido(idPedido)
                .orElseThrow(() -> new PagamentoInexistenteException(idPedido));

        if (Boolean.TRUE.equals(pagamento.estaPago())) {
            throw new PagamentoJaRealizadoException(idPedido);
        }

        pagamento.definirPago();

        pagamentoGateway.salvar(pagamento);

        pagamentoQueue.publicarPagamentoRealizado(new PagamentoRealizadoEvent(pagamento));
    }
}
