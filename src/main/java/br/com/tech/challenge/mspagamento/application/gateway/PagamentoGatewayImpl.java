package br.com.tech.challenge.mspagamento.application.gateway;

import br.com.tech.challenge.mspagamento.application.service.PagamentoService;
import br.com.tech.challenge.mspagamento.application.service.PedidoService;
import br.com.tech.challenge.mspagamento.core.gateway.PagamentoGateway;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

import java.io.File;

@Named
@RequiredArgsConstructor
public class PagamentoGatewayImpl implements PagamentoGateway {
    private final PagamentoService pagamentoService;
    private final PedidoService pedidoService;

    @Override
    public File criarPagamentoPorQrCode(Long idPedido) {
        pedidoService.validarPedido(idPedido);

        return pagamentoService.gerarQrCode(idPedido);
    }
}
