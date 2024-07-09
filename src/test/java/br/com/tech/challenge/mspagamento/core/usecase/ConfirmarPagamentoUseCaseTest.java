package br.com.tech.challenge.mspagamento.core.usecase;

import br.com.tech.challenge.mspagamento.application.service.PagamentoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmarPagamentoUseCaseTest {
    @Mock
    private PagamentoService pagamentoService;

    @Mock
    private RealizarPagamentoUseCase realizarPagamentoUseCase;

    @InjectMocks
    private ConfirmarPagamentoUseCase underTest;

    @Test
    void deveriaRealizarPagamentoComSucesso() {
        var idPedido = 1L;
        when(pagamentoService.confirmarPagamento(anyLong()))
                .thenReturn(idPedido);

        underTest.executar(122L);

        verify(pagamentoService).confirmarPagamento(anyLong());
        verify(realizarPagamentoUseCase).executar(idPedido);
    }
}