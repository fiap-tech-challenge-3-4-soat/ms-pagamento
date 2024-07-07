package br.com.tech.challenge.mspagamento.infrastructure.integration.messaging;

import br.com.tech.challenge.mspagamento.core.domain.Pagamento;
import br.com.tech.challenge.mspagamento.core.domain.Pedido;
import br.com.tech.challenge.mspagamento.core.gateway.PagamentoGateway;
import br.com.tech.challenge.mspagamento.infrastructure.integration.transfer.PedidoTO;
import br.com.tech.challenge.mspagamento.infrastructure.mapper.PedidoModelMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoListenerTest {
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PedidoTO pedidoTO;

    @Mock
    private Pedido pedido;

    @Mock
    private Pagamento pagamento;

    @Mock
    private PedidoModelMapper pedidoModelMapper;

    @Mock
    private PagamentoGateway pagamentoGateway;

    @InjectMocks
    private PedidoListener underTest;

    @Test
    void deveriaRealizarPagamentoComSucesso() throws JsonProcessingException {
        var message = "message";
        when(objectMapper.readValue(message, PedidoTO.class))
                .thenReturn(pedidoTO);
        when(pedidoModelMapper.toDomain(any(PedidoTO.class)))
                .thenReturn(pedido);
        when(pedido.getTotal())
                .thenReturn(BigDecimal.TEN);
        when(pagamentoGateway.gerarDadosQrCode(any(Pagamento.class)))
                .thenReturn(pagamento);
        when(pagamento.getId())
                .thenReturn("idPagamento");
        when(pedido.getId())
                .thenReturn(2L);

        underTest.receive("message");

        verify(objectMapper).readValue(message, PedidoTO.class);
        verify(pedidoModelMapper).toDomain(any(PedidoTO.class));
        verify(pagamento).getId();
        verify(pedido).getId();
        verify(pagamentoGateway).gerarDadosQrCode(any(Pagamento.class));
    }
}