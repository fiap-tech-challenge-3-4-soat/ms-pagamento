package br.com.tech.challenge.mspagamento.application.service;

import br.com.tech.challenge.mspagamento.core.domain.Pagamento;

public interface PagamentoService {
    Pagamento gerarQrCode(Pagamento pagamento);
    Long confirmarPagamento(Long idExterno);
}
