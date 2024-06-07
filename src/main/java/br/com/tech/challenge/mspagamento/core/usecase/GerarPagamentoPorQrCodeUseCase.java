package br.com.tech.challenge.mspagamento.core.usecase;

import br.com.tech.challenge.mspagamento.application.service.PedidoService;
import br.com.tech.challenge.mspagamento.core.domain.Pagamento;
import br.com.tech.challenge.mspagamento.core.domain.StatusPagamento;
import br.com.tech.challenge.mspagamento.core.gateway.PagamentoGateway;

import java.io.File;
import java.math.BigDecimal;

public class GerarPagamentoPorQrCodeUseCase {
    private final PagamentoGateway pagamentoGateway;
    private final PedidoService pedidoService;

    public GerarPagamentoPorQrCodeUseCase(PagamentoGateway pagamentoGateway, PedidoService pedidoService) {
        this.pagamentoGateway = pagamentoGateway;
        this.pedidoService = pedidoService;
    }

    public void executar(Long idPedido) {
        pedidoService.validarPedido(idPedido);

        var pagamento = Pagamento.builder()
                .status(StatusPagamento.ABERTO)
                .total(BigDecimal.ZERO)
                .build();

        pagamentoGateway.gerarQrCode(pagamento);
    }
}
