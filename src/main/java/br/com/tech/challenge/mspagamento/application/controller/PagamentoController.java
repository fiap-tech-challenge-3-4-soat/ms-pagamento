package br.com.tech.challenge.mspagamento.application.controller;


import br.com.tech.challenge.mspagamento.application.service.PagamentoService;
import br.com.tech.challenge.mspagamento.application.service.PedidoService;
import br.com.tech.challenge.mspagamento.core.event.publisher.PagamentoRealizadoEventPublisher;
import br.com.tech.challenge.mspagamento.core.gateway.PagamentoGateway;
import br.com.tech.challenge.mspagamento.core.queue.PagamentoQueue;
import br.com.tech.challenge.mspagamento.core.usecase.ConfirmarPagamentoUseCase;
import br.com.tech.challenge.mspagamento.core.usecase.GerarImagemQrCodeUseCase;
import br.com.tech.challenge.mspagamento.core.usecase.RealizarPagamentoUseCase;
import jakarta.inject.Named;

import java.io.File;

@Named
public class PagamentoController {
    private final PagamentoGateway pagamentoGateway;
    private final PagamentoQueue pagamentoQueue;
    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoGateway pagamentoGateway, PagamentoQueue pagamentoQueue, PagamentoService pagamentoService) {
        this.pagamentoGateway = pagamentoGateway;
        this.pagamentoQueue = pagamentoQueue;
        this.pagamentoService = pagamentoService;
    }

    public File gerarImagemQrCode(Long idPedido) {
        var gerarPagamentoPorQrCodeUseCase = new GerarImagemQrCodeUseCase(this.pagamentoGateway);

        return gerarPagamentoPorQrCodeUseCase.executar(idPedido);
    }

    public void pagar(Long idPedido) {
        var realizarPagamentoUseCase = new RealizarPagamentoUseCase(this.pagamentoGateway, pagamentoQueue);

        realizarPagamentoUseCase.executar(idPedido);
    }

    public void receberConfirmacaoPagamento(Long idExterno) {
        var realizarPagamentoUseCase = new RealizarPagamentoUseCase(this.pagamentoGateway, pagamentoQueue);
        //TODO remover confirmacao
        var confirmarPagamentoUseCase = new ConfirmarPagamentoUseCase(this.pagamentoService, realizarPagamentoUseCase);

        confirmarPagamentoUseCase.executar(idExterno);
    }
}
