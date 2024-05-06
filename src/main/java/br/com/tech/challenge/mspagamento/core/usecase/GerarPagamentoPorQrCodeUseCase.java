package br.com.tech.challenge.mspagamento.core.usecase;

import br.com.tech.challenge.mspagamento.core.gateway.PagamentoGateway;

import java.io.File;

public class GerarPagamentoPorQrCodeUseCase {
    private final PagamentoGateway pagamentoGateway;

    public GerarPagamentoPorQrCodeUseCase(PagamentoGateway pagamentoGateway) {
        this.pagamentoGateway = pagamentoGateway;
    }

    public File executar(Long idPedido) {
        return pagamentoGateway.criarPagamentoPorQrCode(idPedido);
    }
}
