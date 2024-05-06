package br.com.tech.challenge.mspagamento.application.v1.controller;

import br.com.tech.challenge.mspagamento.application.controller.PagamentoController;
import br.com.tech.challenge.mspagamento.core.gateway.PagamentoGateway;
import br.com.tech.challenge.mspagamento.core.usecase.GerarPagamentoPorQrCodeUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagamentoControllerTest {
    @Mock
    private PagamentoGateway pagamentoGateway;

    @Mock
    private File file;

    @InjectMocks
    private PagamentoController controller;


    @Test
    void deveriaGerarPagamentoComSucesso() {
        when(pagamentoGateway.criarPagamentoPorQrCode(anyLong()))
                .thenReturn(file);

        controller.gerarPagamento(1L);

        verify(pagamentoGateway).criarPagamentoPorQrCode(anyLong());
    }
}