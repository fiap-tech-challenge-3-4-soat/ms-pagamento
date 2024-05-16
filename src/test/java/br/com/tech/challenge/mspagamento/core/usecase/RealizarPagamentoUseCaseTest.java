package br.com.tech.challenge.mspagamento.core.usecase;

import br.com.tech.challenge.mspagamento.core.domain.Pagamento;
import br.com.tech.challenge.mspagamento.core.event.PagamentoRealizadoEvent;
import br.com.tech.challenge.mspagamento.core.event.publisher.PagamentoRealizadoEventPublisher;
import br.com.tech.challenge.mspagamento.core.exception.PagamentoInexistenteException;
import br.com.tech.challenge.mspagamento.core.exception.PagamentoJaRealizadoException;
import br.com.tech.challenge.mspagamento.core.gateway.PagamentoGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RealizarPagamentoUseCaseTest {
    @Mock
    private PagamentoGateway pagamentoGateway;

    @Mock
    private PagamentoRealizadoEventPublisher pagamentoRealizadoEventPublisher;

    @Mock
    private Pagamento pagamento;

    @InjectMocks
    private RealizarPagamentoUseCase underTest;

    @Test
    void deveriaRealizarPagamentoComSucesso() {
        doNothing().when(pagamento).definirPago();
        when(pagamentoGateway.obterPagamentoPorIdPedido(anyLong()))
                .thenReturn(Optional.of(pagamento));

        underTest.executar(1L);

        verify(pagamentoGateway).obterPagamentoPorIdPedido(anyLong());
        verify(pagamentoGateway).salvar(any(Pagamento.class));
        verify(pagamento).definirPago();
        verify(pagamento).estaPago();
        verify(pagamentoRealizadoEventPublisher).publicar(any(PagamentoRealizadoEvent.class));
    }

    @Test
    void deveriaFalharQuandoNaoEncontrarPagamento() {
        var idPedidoInvalido = 2L;
        when(pagamentoGateway.obterPagamentoPorIdPedido(anyLong()))
                .thenReturn(Optional.empty());

        //TODO verificar
        var exception = assertThrows(PagamentoInexistenteException.class,
                () -> underTest.executar(idPedidoInvalido));

        assertThat(exception.getMessage()).contains(String.valueOf(idPedidoInvalido));

        verify(pagamentoGateway).obterPagamentoPorIdPedido(anyLong());
        verify(pagamentoGateway, never()).salvar(any(Pagamento.class));
        verify(pagamento, never()).estaPago();
        verify(pagamento, never()).definirPago();
        verify(pagamentoRealizadoEventPublisher, never()).publicar(any(PagamentoRealizadoEvent.class));
    }

    @Test
    void deveriaFalharQuandoOPagamentoJaEstaPago() {
        when(pagamentoGateway.obterPagamentoPorIdPedido(anyLong()))
                .thenReturn(Optional.of(pagamento));
        when(pagamento.estaPago())
                .thenReturn(Boolean.TRUE);

        var exception = assertThrows(PagamentoJaRealizadoException.class,
                () -> underTest.executar(1L));

        verify(pagamentoGateway).obterPagamentoPorIdPedido(anyLong());
        verify(pagamentoGateway, never()).salvar(any(Pagamento.class));
        verify(pagamento).estaPago();
        verify(pagamento, never()).definirPago();
        verify(pagamentoRealizadoEventPublisher, never()).publicar(any(PagamentoRealizadoEvent.class));
    }
}