package br.com.tech.challenge.mspagamento.core.usecase;

import br.com.tech.challenge.mspagamento.application.service.PagamentoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmarPagamentoUseCaseTest {
    @Mock
    private PagamentoService pagamentoService;

    @Mock
    private RealizarPagamentoUseCase realizarPagamentoUseCase;

    @InjectMocks
    private ConfirmarPagamentoUseCase underTest;

    @Test
    void deveriaReceberConfirmacaoPagamentoComSucesso() {
        var idPedido = 10L;
        when(pagamentoService.confirmarPagamento(anyLong()))
                .thenReturn(idPedido);
        doNothing().when(realizarPagamentoUseCase).executar(idPedido);

        underTest.executar(1L);

        verify(pagamentoService).confirmarPagamento(anyLong());
        verify(realizarPagamentoUseCase).executar(idPedido);
    }
}