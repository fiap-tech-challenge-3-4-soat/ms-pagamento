package br.com.tech.challenge.mspagamento.infrastructure.integration.messaging;

import br.com.tech.challenge.mspagamento.core.domain.Pagamento;
import br.com.tech.challenge.mspagamento.core.domain.StatusPagamento;
import br.com.tech.challenge.mspagamento.core.gateway.PagamentoGateway;
import br.com.tech.challenge.mspagamento.infrastructure.integration.transfer.PedidoTO;
import br.com.tech.challenge.mspagamento.infrastructure.mapper.PedidoModelMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class PedidoListener {
    private final ObjectMapper objectMapper;
    private final PedidoModelMapper pedidoModelMapper;
    private final PagamentoGateway pagamentoGateway;

    @RabbitListener(queues = {"${queue.filas.pedidos_criados}"})
    public void receive(String message) throws JsonProcessingException {
        var pedidoTO = objectMapper.readValue(message, PedidoTO.class);
        var pedido = pedidoModelMapper.toDomain(pedidoTO);

        var pagamento = Pagamento.builder()
                        .total(pedido.getTotal())
                        .status(StatusPagamento.ABERTO)
                        .pedido(pedido)
                        .build();

        var pagamentoSalvo = pagamentoGateway.gerarDadosQrCode(pagamento);

        log.info(String.format("Pagamento de id %s criado com sucesso. Pedido id %d", pagamentoSalvo.getId(), pedido.getId()));
    }
}