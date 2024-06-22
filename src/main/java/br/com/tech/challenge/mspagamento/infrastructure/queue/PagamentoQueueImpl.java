package br.com.tech.challenge.mspagamento.infrastructure.queue;

import br.com.tech.challenge.mspagamento.core.domain.Pagamento;
import br.com.tech.challenge.mspagamento.core.event.PagamentoRealizadoEvent;
import br.com.tech.challenge.mspagamento.core.queue.PagamentoQueue;
import br.com.tech.challenge.mspagamento.infrastructure.exception.InternalErrorException;
import br.com.tech.challenge.mspagamento.infrastructure.mapper.PagamentoModelMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class PagamentoQueueImpl implements PagamentoQueue {
    private final RabbitTemplate rabbitTemplate;
    private final PagamentoModelMapper pagamentoMapper;
    private final ObjectMapper jsonMapper;

    @Value("${queue.filas.pagamentos_confirmados}")
    private String pagamentosConfirmados;

    @Value("${queue.exchange.fanoutPagamento}")
    private String queuePagamentoExchange;

    @Override
    public void publicarPagamentoRealizado(PagamentoRealizadoEvent pagamentoRealizadoEvent) {
        try {
            var confirmacaoPagamentoJson = jsonMapper.writeValueAsString(pagamentoMapper.toPagamentoConfirmadoDTO(pagamentoRealizadoEvent.pagamento()));
            rabbitTemplate.convertAndSend(queuePagamentoExchange, "", confirmacaoPagamentoJson);
            log.info(String.format("Publicação na fila %s executada", pagamentosConfirmados));
        } catch (Exception exception) {
            log.error(exception);
            throw new InternalErrorException(String.format("Não foi possível enviar para a fila %s", pagamentosConfirmados));
        }
    }
}
