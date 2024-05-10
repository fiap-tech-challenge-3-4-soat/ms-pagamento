package br.com.tech.challenge.mspagamento.application.service;

import br.com.tech.challenge.mspagamento.core.domain.Pagamento;

import java.io.File;

public interface PagamentoService {
    File gerarQrCode(Pagamento pagamento);
    Long confirmarPagamento(Long idExterno);
}
