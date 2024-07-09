package br.com.tech.challenge.mspagamento.core.usecase;

import br.com.tech.challenge.mspagamento.core.domain.Pagamento;
import br.com.tech.challenge.mspagamento.core.exception.PagamentoInexistenteException;
import br.com.tech.challenge.mspagamento.core.gateway.PagamentoGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GerarImagemQrCodeUseCaseTest {
    @Mock
    private PagamentoGateway pagamentoGateway;

    @Mock
    private Pagamento pagamento;

    @InjectMocks
    private GerarImagemQrCodeUseCase underTest;

    @Test
    void deveriaRealizarPagamentoComSucesso() {
        when(pagamentoGateway.obterPagamentoPorIdPedido(anyLong()))
                .thenReturn(Optional.of(pagamento));
        when(pagamento.getQrCode())
                .thenReturn("qrCodeData");

        underTest.executar(1L);

        verify(pagamentoGateway).obterPagamentoPorIdPedido(anyLong());
        verify(pagamentoGateway).gerarImagemQrCode(anyString(), anyLong());
    }

    @Test
    void deveriaFalharQuandoNaoEncontrarPagamento() {
        var idPedidoInvalido = 2L;
        when(pagamentoGateway.obterPagamentoPorIdPedido(anyLong()))
                .thenReturn(Optional.empty());

        var exception = assertThrows(PagamentoInexistenteException.class,
                () -> underTest.executar(idPedidoInvalido));

        assertThat(exception.getMessage()).contains(String.valueOf(idPedidoInvalido));

        verify(pagamentoGateway).obterPagamentoPorIdPedido(anyLong());
        verify(pagamentoGateway, never()).gerarImagemQrCode(anyString(), anyLong());
    }
}