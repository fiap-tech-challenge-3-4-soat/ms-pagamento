package br.com.tech.challenge.mspagamento.application.controller;


import br.com.tech.challenge.mspagamento.application.service.PagamentoService;
import br.com.tech.challenge.mspagamento.application.service.PedidoService;
import br.com.tech.challenge.mspagamento.core.event.publisher.PagamentoRealizadoEventPublisher;
import br.com.tech.challenge.mspagamento.core.gateway.PagamentoGateway;
import br.com.tech.challenge.mspagamento.core.usecase.ConfirmarPagamentoUseCase;
import br.com.tech.challenge.mspagamento.core.usecase.GerarPagamentoPorQrCodeUseCase;
import br.com.tech.challenge.mspagamento.core.usecase.RealizarPagamentoUseCase;
import jakarta.inject.Named;

import java.io.File;

@Named
public class PagamentoController {
    private final PagamentoGateway pagamentoGateway;
    private final PedidoService pedidoService;
    private final PagamentoService pagamentoService;
    private final PagamentoRealizadoEventPublisher pagamentoRealizadoEventPublisher;

    public PagamentoController(PagamentoGateway pagamentoGateway, PedidoService pedidoService, PagamentoService pagamentoService, PagamentoRealizadoEventPublisher pagamentoRealizadoEventPublisher) {
        this.pagamentoGateway = pagamentoGateway;
        this.pedidoService = pedidoService;
        this.pagamentoService = pagamentoService;
        this.pagamentoRealizadoEventPublisher = pagamentoRealizadoEventPublisher;
    }

    public void pagar(Long idPedido) {
        var realizarPagamentoUseCase = new RealizarPagamentoUseCase(this.pagamentoGateway, pagamentoRealizadoEventPublisher);

        realizarPagamentoUseCase.executar(idPedido);
    }

    public void receberConfirmacaoPagamento(Long idExterno) {
        var realizarPagamentoUseCase = new RealizarPagamentoUseCase(this.pagamentoGateway, pagamentoRealizadoEventPublisher);
        var confirmarPagamentoUseCase = new ConfirmarPagamentoUseCase(this.pagamentoService, realizarPagamentoUseCase);

        confirmarPagamentoUseCase.executar(idExterno);
    }
}
