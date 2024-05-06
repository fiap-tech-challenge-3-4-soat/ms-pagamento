package br.com.tech.challenge.mspagamento.application.controller;


import br.com.tech.challenge.mspagamento.core.gateway.PagamentoGateway;
import br.com.tech.challenge.mspagamento.core.usecase.GerarPagamentoPorQrCodeUseCase;
import jakarta.inject.Named;

import java.io.File;

@Named
public class PagamentoController {
    private final PagamentoGateway pagamentoGateway;

    public PagamentoController(PagamentoGateway pagamentoGateway) {
        this.pagamentoGateway = pagamentoGateway;
    }

    public File gerarPagamento(Long idPedido) {
        var gerarPagamentoPorQrCodeUseCase = new GerarPagamentoPorQrCodeUseCase(this.pagamentoGateway);

        return gerarPagamentoPorQrCodeUseCase.executar(idPedido);
    }
}
