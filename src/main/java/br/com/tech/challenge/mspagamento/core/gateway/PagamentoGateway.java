package br.com.tech.challenge.mspagamento.core.gateway;

import br.com.tech.challenge.mspagamento.core.domain.Pagamento;

import java.util.Optional;

public interface PagamentoGateway {
    Pagamento gerarQrCode(Pagamento pagamento);
    Optional<Pagamento> obterPagamentoPorIdPedido(Long idPedido);
    Pagamento salvar(Pagamento pagamento);
}
