package br.com.tech.challenge.mspagamento.core.usecase;

import br.com.tech.challenge.mspagamento.core.exception.PagamentoInexistenteException;
import br.com.tech.challenge.mspagamento.core.gateway.PagamentoGateway;

import java.io.File;

public class GerarImagemQrCodeUseCase {
    private final PagamentoGateway pagamentoGateway;

    public GerarImagemQrCodeUseCase(PagamentoGateway pagamentoGateway) {
        this.pagamentoGateway = pagamentoGateway;
    }

    public File executar(Long idPedido) {
        var pagamento = pagamentoGateway.obterPagamentoPorIdPedido(idPedido)
                .orElseThrow(() -> new PagamentoInexistenteException(idPedido));

        return pagamentoGateway.gerarImagemQrCode(pagamento.getQrCode(), idPedido);
    }
}
