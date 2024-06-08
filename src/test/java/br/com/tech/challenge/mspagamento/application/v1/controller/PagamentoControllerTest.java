package br.com.tech.challenge.mspagamento.application.v1.controller;

import br.com.tech.challenge.mspagamento.application.controller.PagamentoController;
import br.com.tech.challenge.mspagamento.application.service.PagamentoService;
import br.com.tech.challenge.mspagamento.application.service.PedidoService;
import br.com.tech.challenge.mspagamento.core.domain.Pagamento;
import br.com.tech.challenge.mspagamento.core.event.PagamentoRealizadoEvent;
import br.com.tech.challenge.mspagamento.core.event.publisher.PagamentoRealizadoEventPublisher;
import br.com.tech.challenge.mspagamento.core.gateway.PagamentoGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoControllerTest {
    @Mock
    private PagamentoGateway pagamentoGateway;

    @Mock
    private PagamentoService pagamentoService;

    @Mock
    private PagamentoRealizadoEventPublisher pagamentoRealizadoEventPublisher;

    @Mock
    private File file;

    @Mock
    private Pagamento pagamento;

    @InjectMocks
    private PagamentoController controller;


    @Test
    void deveriaGerarImagemQrCodeComSucesso() {
        when(pagamentoGateway.obterPagamentoPorIdPedido(anyLong()))
                .thenReturn(Optional.of(pagamento));
        when(pagamento.getQrCode())
                .thenReturn("QrCodeData");
        when(pagamentoGateway.gerarImagemQrCode(anyString(), anyLong()))
                .thenReturn(file);

        controller.gerarImagemQrCode(1L);

        verify(pagamentoGateway).obterPagamentoPorIdPedido(anyLong());
        verify(pagamentoGateway).gerarImagemQrCode(anyString(), anyLong());
    }

    @Test
    void deveriaRealizarPagamentoComSucesso() {
        doNothing().when(pagamento).definirPago();
        when(pagamentoGateway.obterPagamentoPorIdPedido(anyLong()))
                .thenReturn(Optional.of(pagamento));
        when(pagamento.estaPago())
                .thenReturn(Boolean.FALSE);

        controller.pagar(1L);

        verify(pagamentoGateway).obterPagamentoPorIdPedido(anyLong());
        verify(pagamentoGateway).salvar(any(Pagamento.class));
        verify(pagamento).definirPago();
        verify(pagamento).estaPago();
        verify(pagamentoRealizadoEventPublisher).publicar(any(PagamentoRealizadoEvent.class));
    }

    @Test
    void deveriaReceberConfirmacaoPagamentoComSucesso() {
        var idPedido = 10L;
        doNothing().when(pagamento).definirPago();
        when(pagamentoService.confirmarPagamento(anyLong()))
                .thenReturn(idPedido);
        when(pagamentoGateway.obterPagamentoPorIdPedido(anyLong()))
                .thenReturn(Optional.of(pagamento));

        controller.receberConfirmacaoPagamento(1L);

        verify(pagamentoService).confirmarPagamento(anyLong());
        verify(pagamentoGateway).obterPagamentoPorIdPedido(anyLong());
        verify(pagamento).definirPago();
        verify(pagamentoRealizadoEventPublisher).publicar(any(PagamentoRealizadoEvent.class));
    }
}