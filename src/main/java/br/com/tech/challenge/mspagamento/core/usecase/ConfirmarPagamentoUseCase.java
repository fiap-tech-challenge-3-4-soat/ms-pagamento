package br.com.tech.challenge.mspagamento.core.usecase;

import br.com.tech.challenge.mspagamento.application.service.PagamentoService;

public class ConfirmarPagamentoUseCase {
    private final PagamentoService pagamentoService;
    private final RealizarPagamentoUseCase realizarPagamentoUseCase;

    public ConfirmarPagamentoUseCase(PagamentoService pagamentoService, RealizarPagamentoUseCase realizarPagamentoUseCase) {
        this.pagamentoService = pagamentoService;
        this.realizarPagamentoUseCase = realizarPagamentoUseCase;
    }

    public void executar(Long idExterno) {
        var idPedido = pagamentoService.confirmarPagamento(idExterno);

        realizarPagamentoUseCase.executar(idPedido);
    }
}
