package br.com.tech.challenge.mspagamento.infrastructure.service;

import br.com.tech.challenge.mspagamento.core.domain.Pagamento;
import br.com.tech.challenge.mspagamento.infrastructure.exception.IntegrationException;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.ConsultaMerchantOrderResponse;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.GerarCodigoQrRequest;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.GerarCodigoQrResponse;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.MercadoPagoHttpClient;
import br.com.tech.challenge.mspagamento.infrastructure.mapper.PagamentoModelMapper;
import br.com.tech.challenge.mspagamento.infrastructure.persistence.model.PagamentoModel;
import br.com.tech.challenge.mspagamento.infrastructure.persistence.repository.mongodb.PagamentoRepositoryMongoDB;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static br.com.tech.challenge.mspagamento.TestObjects.obterGerarCodigoQrResponse;
import static br.com.tech.challenge.mspagamento.TestObjects.obterPagamento;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoMercadoLivreServiceTest {
    private Pagamento pagamento;

    private GerarCodigoQrResponse gerarCodigoQrResponse;

    @Mock
    private PagamentoModel pagamentoModel;

    @Mock
    private MercadoPagoHttpClient mercadopagoHttpClient;

    @Mock
    private PagamentoRepositoryMongoDB pagamentoRepositoryMongoDB;

    @Mock
    private PagamentoModelMapper mapper;

    @InjectMocks
    private PagamentoMercadoLivreService underTest;

    @BeforeEach
    void setUp() {
        pagamento = obterPagamento();
        gerarCodigoQrResponse = obterGerarCodigoQrResponse();
    }

    @Test
    void deveriaGerarDadosDoQrCodeComSucesso() {
        when(mercadopagoHttpClient.gerarQrData(any(GerarCodigoQrRequest.class)))
                .thenReturn(ResponseEntity.of(Optional.of(gerarCodigoQrResponse)));
        when(pagamentoRepositoryMongoDB.save(any(PagamentoModel.class)))
                .thenReturn(pagamentoModel);
        when(mapper.toModel(pagamento))
                .thenReturn(pagamentoModel);
        when(mapper.toDomain(pagamentoModel))
                .thenReturn(pagamento);

        underTest.gerarDadosQrCode(pagamento);

        verify(mercadopagoHttpClient).gerarQrData(any(GerarCodigoQrRequest.class));
        verify(pagamentoRepositoryMongoDB).save(any(PagamentoModel.class));
        verify(mapper).toModel(pagamento);
        verify(mapper).toDomain(pagamentoModel);
    }

    @Test
    void deveriaFalharQuandoOcorrerErrosNaRequisicao() {
        when(mercadopagoHttpClient.gerarQrData(any(GerarCodigoQrRequest.class)))
                .thenThrow(FeignException.class);

        assertThrows(IntegrationException.class,
                () -> underTest.gerarDadosQrCode(pagamento));

        verify(mercadopagoHttpClient).gerarQrData(any(GerarCodigoQrRequest.class));
        verify(pagamentoRepositoryMongoDB, never()).save(any(PagamentoModel.class));
        verify(mapper, never()).toModel(pagamento);
        verify(mapper, never()).toDomain(pagamentoModel);
    }

    @Test
    void deveriaFalharQuandoOsDadosDaRequisicaoForemInvalidos() {
        when(mercadopagoHttpClient.gerarQrData(any(GerarCodigoQrRequest.class)))
                .thenReturn(ResponseEntity.of(Optional.empty()));

        var exception = assertThrows(IntegrationException.class,
                () -> underTest.gerarDadosQrCode(pagamento));

        assertThat(exception.getMessage()).isEqualTo("Não foi possível obter os dados para gerar o QR-Code");

        verify(mercadopagoHttpClient).gerarQrData(any(GerarCodigoQrRequest.class));
        verify(pagamentoRepositoryMongoDB, never()).save(any(PagamentoModel.class));
        verify(mapper, never()).toModel(pagamento);
        verify(mapper, never()).toDomain(pagamentoModel);
    }

    @Test
    void deveriaFalharQuandoOcorrerErrosNaRequisicaoDaConfirmacaoDePagamento() {
        var idExterno = 10L;
        when(mercadopagoHttpClient.consultarMerchantOrder(idExterno))
                .thenThrow(FeignException.class);

        assertThrows(IntegrationException.class,
                () -> underTest.confirmarPagamento(idExterno));

        verify(mercadopagoHttpClient).consultarMerchantOrder(idExterno);
        verify(pagamentoRepositoryMongoDB, never()).save(any(PagamentoModel.class));
        verify(mapper, never()).toModel(pagamento);
        verify(mapper, never()).toDomain(pagamentoModel);
    }

    @Test
    void deveriaFalharQuandoPedidoRetornarComoCancelado() {
        var idExterno = 10L;
        var consultaMerchantOrderResponse = new ConsultaMerchantOrderResponse("idExterno", "closed", true, "paid", 10L);

        when(mercadopagoHttpClient.consultarMerchantOrder(idExterno))
                .thenReturn(ResponseEntity.of(Optional.of(consultaMerchantOrderResponse)));

        var resposta = underTest.confirmarPagamento(idExterno);

        assertThat(resposta).isNull();

        verify(mercadopagoHttpClient).consultarMerchantOrder(idExterno);
        verify(pagamentoRepositoryMongoDB, never()).save(any(PagamentoModel.class));
        verify(mapper, never()).toModel(pagamento);
        verify(mapper, never()).toDomain(pagamentoModel);
    }

    @Test
    void deveriaFalharQuandoPedidoRetornarComOStatusAberto() {
        var idExterno = 10L;
        var consultaMerchantOrderResponse = new ConsultaMerchantOrderResponse("idExterno", "open", false, "paid", 10L);

        when(mercadopagoHttpClient.consultarMerchantOrder(idExterno))
                .thenReturn(ResponseEntity.of(Optional.of(consultaMerchantOrderResponse)));

        var resposta = underTest.confirmarPagamento(idExterno);

        assertThat(resposta).isNull();

        verify(mercadopagoHttpClient).consultarMerchantOrder(idExterno);
        verify(pagamentoRepositoryMongoDB, never()).save(any(PagamentoModel.class));
        verify(mapper, never()).toModel(pagamento);
        verify(mapper, never()).toDomain(pagamentoModel);
    }
}