package br.com.tech.challenge.mspagamento.core.usecase;

import br.com.tech.challenge.mspagamento.core.gateway.PagamentoGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GerarPagamentoPorQrCodeUseCaseTest {
    @Mock
    private PagamentoGateway pagamentoGateway;

    @Mock
    private File file;

    @InjectMocks
    private GerarPagamentoPorQrCodeUseCase underTest;

    @Test
    void deveriaGerarPagamentoComSucesso() {
        when(pagamentoGateway.gerarQrCode(anyLong()))
                .thenReturn(file);

        underTest.executar(1L);

        verify(pagamentoGateway).gerarQrCode(anyLong());
    }
}