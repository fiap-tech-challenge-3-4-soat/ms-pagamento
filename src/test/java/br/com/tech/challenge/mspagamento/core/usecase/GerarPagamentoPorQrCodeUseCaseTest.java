package br.com.tech.challenge.mspagamento.core.usecase;

import br.com.tech.challenge.mspagamento.application.service.PedidoService;
import br.com.tech.challenge.mspagamento.core.domain.Pagamento;
import br.com.tech.challenge.mspagamento.core.gateway.PagamentoGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GerarPagamentoPorQrCodeUseCaseTest {
    @Mock
    private PagamentoGateway pagamentoGateway;

    @Mock
    private PedidoService pedidoService;

    @Mock
    private File file;

    @Mock
    private Pagamento pagamento;

    @InjectMocks
    private GerarPagamentoPorQrCodeUseCase underTest;

    @Test
    void deveriaGerarPagamentoComSucesso() {
        doNothing().when(pedidoService).validarPedido(anyLong());
        when(pagamentoGateway.gerarQrCode(any(Pagamento.class)))
                .thenReturn(file);

        underTest.executar(1L);

        verify(pagamentoGateway).gerarQrCode(any(Pagamento.class));
        verify(pedidoService).validarPedido(anyLong());
    }
}