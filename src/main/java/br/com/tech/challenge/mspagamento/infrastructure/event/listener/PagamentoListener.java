package br.com.tech.challenge.mspagamento.infrastructure.event.listener;

import br.com.tech.challenge.mspagamento.application.service.PedidoService;
import br.com.tech.challenge.mspagamento.core.event.PagamentoRealizadoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagamentoListener {
    private final PedidoService pedidoService;

    @EventListener
    public void pagamentoRealizadoListener(PagamentoRealizadoEvent event) {
        pedidoService.definirPedidoComoPago(event.pagamento().getIdPedido());
    }
}
