package br.com.tech.challenge.mspagamento.core.gateway;

import br.com.tech.challenge.mspagamento.core.domain.Pagamento;

import java.io.File;
import java.util.Optional;

public interface PagamentoGateway {
    Pagamento gerarDadosQrCode(Pagamento pagamento);
    File gerarImagemQrCode(String qrCodeData, Long idPedido);
    Optional<Pagamento> obterPagamentoPorIdPedido(Long idPedido);
    Pagamento salvar(Pagamento pagamento);
}
