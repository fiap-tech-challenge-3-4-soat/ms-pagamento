package br.com.tech.challenge.mspagamento.infrastructure.service;

import br.com.tech.challenge.mspagamento.application.service.PedidoService;
import br.com.tech.challenge.mspagamento.core.exception.PagamentoJaRealizadoException;
import br.com.tech.challenge.mspagamento.infrastructure.exception.IntegrationException;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mspedido.MSPedidoHttpClient;
import feign.RetryableException;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Named
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {
    private final MSPedidoHttpClient msPedidoHttpClient;

    @Override
    public void validarPedido(Long idPedido) {
        try {
            var response = msPedidoHttpClient.obterStatusPedido(idPedido);
            var statusPedido = response.getBody();

            if (Objects.isNull(statusPedido)) {
                throw new IntegrationException("Não foi possível obter os dados para validar o pedido");
            }

            if (Boolean.TRUE.equals(statusPedido.pagamentoAprovado())) {
                throw new PagamentoJaRealizadoException(idPedido);
            }
        } catch (RetryableException exception) {
            throw new IntegrationException("Não foi possível obter os dados para validar o pedido");
        }
    }

    @Override
    public void definirPedidoComoPago(Long idPedido) {
        try {
            msPedidoHttpClient.confirmarPagamento(idPedido);
        } catch (RetryableException exception) {
            throw new IntegrationException("Não foi possível obter os dados para validar o pedido");
        }
    }
}
