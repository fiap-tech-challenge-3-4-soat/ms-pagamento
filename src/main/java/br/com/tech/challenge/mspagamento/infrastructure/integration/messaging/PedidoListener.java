package br.com.tech.challenge.mspagamento.infrastructure.integration.messaging;

import br.com.tech.challenge.mspagamento.application.controller.PagamentoController;
import br.com.tech.challenge.mspagamento.application.service.PedidoService;
import br.com.tech.challenge.mspagamento.core.usecase.GerarPagamentoPorQrCodeUseCase;
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
    //private final GerarPagamentoPorQrCodeUseCase gerarPagamentoPorQrCodeUseCase;

    @RabbitListener(queues = {"${queue.fila.pedidos_criados}"})
    public void receive(String message) throws JsonProcessingException {
        var pedidoTO = objectMapper.readValue(message, PedidoTO.class);
        var pedido = pedidoModelMapper.toDomain(pedidoTO);


        log.info(String.format("Pagamento %d criado com sucesso", pedido.getId()));
    }
}
