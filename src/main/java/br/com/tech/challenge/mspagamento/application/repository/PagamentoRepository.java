package br.com.tech.challenge.mspagamento.application.repository;

import br.com.tech.challenge.mspagamento.core.domain.Pagamento;

import java.util.Optional;

public interface PagamentoRepository {
    Optional<Pagamento> obterPagamentoPorIdPedido(Long idPedido);
    Pagamento salvar(Pagamento pagamento);
}
