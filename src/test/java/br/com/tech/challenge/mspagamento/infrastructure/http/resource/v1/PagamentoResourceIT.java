package br.com.tech.challenge.mspagamento.infrastructure.http.resource.v1;

import br.com.tech.challenge.mspagamento.TestObjects;
import br.com.tech.challenge.mspagamento.core.domain.Pagamento;
import br.com.tech.challenge.mspagamento.core.gateway.PagamentoGateway;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.EventoConfirmacaoPagamento;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.GerarCodigoQrRequest;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.MercadoPagoHttpClient;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.qrcodeapi.QrCodeHttpClient;
import br.com.tech.challenge.mspagamento.infrastructure.persistence.repository.mongodb.PagamentoRepositoryMongoDB;
import feign.FeignException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static br.com.tech.challenge.mspagamento.TestObjects.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PagamentoResourceIT {
    private final String pagamentoPath = "/v1/pagamentos";

    private Pagamento pagamento;

    @Mock
    private FeignException feignException;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MercadoPagoHttpClient mercadopagoHttpClient;

    @MockBean
    private QrCodeHttpClient qrCodeHttpClient;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PagamentoRepositoryMongoDB pagamentoRepositoryMongoDB;

    @Autowired
    private PagamentoGateway pagamentoGateway;

    private File file;

    @Value("${assets.image}")
    private String imagePath;

    private Long idPedido;

    @BeforeEach
    public void setUp() throws Exception {
        pagamento = TestObjects.obterPagamento();
        pagamentoGateway.salvar(pagamento);
        idPedido = pagamento.getPedido().getId();
        this.file = new File(imagePath + pagamento.getPedido().getId() + ".png");
        file.createNewFile();
    }

    @AfterEach
    public void tearDown() {
        this.file.delete();
        pagamentoRepositoryMongoDB.deleteAll();
    }

    @Test
    void deveriaGerarImagemQrCodeDePagamentoComSucesso() throws Exception {
        var gerarQrCodeResponse = obterGerarCodigoQrResponse();
        var arrayByte = new byte[1];

        when(mercadopagoHttpClient.gerarQrData(any(GerarCodigoQrRequest.class)))
                .thenReturn(ResponseEntity.of(Optional.of(gerarQrCodeResponse)));
        when(qrCodeHttpClient.gerarQrCode(anyString(), anyString()))
                .thenReturn(ResponseEntity.of(Optional.of(arrayByte)));

        try (MockedStatic<ImageIO> imageIO = Mockito.mockStatic(ImageIO.class)) {
            imageIO.when(() -> ImageIO.read(any(ByteArrayInputStream.class)))
                    .thenReturn(obterBufferedImage());

            mockMvc.perform(post(pagamentoPath + "/{idPedido}/qrcode", idPedido)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE));
        }

        var pagamentoOptional = obterPagamento();

        verify(qrCodeHttpClient).gerarQrCode(anyString(), anyString());

        assertTrue(pagamentoOptional.isPresent());
    }

    @Test
    void deveriaFalharQuandoARespostaParaGerarAImagemDoQrCodeForInvalido() throws Exception {
        when(qrCodeHttpClient.gerarQrCode(anyString(), anyString()))
                .thenReturn(ResponseEntity.of(Optional.empty()));

        mockMvc.perform(post(pagamentoPath + "/{idPedido}/qrcode", idPedido)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensagem").value("Não foi possível obter a imagem do QR-Code"));

        verify(qrCodeHttpClient).gerarQrCode(anyString(), anyString());
    }

    @Test
    void deveriaFalharQuandoOcorrerErrosNaRequisicaoParaGerarImagemQrCode() throws Exception {
        when(qrCodeHttpClient.gerarQrCode(anyString(), anyString()))
                .thenThrow(FeignException.class);

        mockMvc.perform(post(pagamentoPath + "/{idPedido}/qrcode", idPedido)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError());

        verify(qrCodeHttpClient).gerarQrCode(anyString(), anyString());
    }

    @Test
    void deveriaReceberConfirmacaoDePagamentoQuandoOTopicForMockComSucesso() throws Exception {
        pagamentoGateway.obterPagamentoPorIdPedido(idPedido).get();

        assertFalse(pagamento.estaPago());

        mockMvc.perform(post(pagamentoPath + "/confirmar-pagamento")
                        .param("id", String.valueOf(idPedido))
                        .param("topic", EventoConfirmacaoPagamento.MOCK.name())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        pagamento = pagamentoGateway.obterPagamentoPorIdPedido(idPedido).get();

        assertTrue(pagamento.estaPago());
    }

    @Test
    void deveriaFalharQuandoReceberConfirmacaoDePagamentoComIdPedidoInvalido() throws Exception {
        var idPedidoInvalido = 111L;
        mockMvc.perform(post(pagamentoPath + "/confirmar-pagamento")
                        .param("id", String.valueOf(idPedidoInvalido))
                        .param("topic", EventoConfirmacaoPagamento.MOCK.name())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensagem").value(String.format("Pagamento inexistente, id do pedido: %s", idPedidoInvalido)));


        var pagamentoOptional = pagamentoGateway.obterPagamentoPorIdPedido(idPedidoInvalido);

        assertFalse(pagamentoOptional.isPresent());
    }

    @Test
    void deveriaReceberConfirmacaoDePagamentoComSucesso() throws Exception {
        var consultaMerchantOrderResponse = obterConsultaMerchantOrderResponse(idPedido);

        assertFalse(pagamento.estaPago());

        when(mercadopagoHttpClient.consultarMerchantOrder(anyLong()))
                .thenReturn(ResponseEntity.of(Optional.of(consultaMerchantOrderResponse)));

        mockMvc.perform(post(pagamentoPath + "/confirmar-pagamento")
                        .param("id", String.valueOf(idPedido))
                        .param("topic", EventoConfirmacaoPagamento.MERCHANT_ORDER.name())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        pagamento = pagamentoGateway.obterPagamentoPorIdPedido(idPedido).get();

        assertTrue(pagamento.estaPago());
    }

    @Test
    void deveriaRetornarQuandoTopicForPayment() throws Exception {
        mockMvc.perform(post(pagamentoPath + "/confirmar-pagamento")
                        .param("id", String.valueOf(111L))
                        .param("topic", EventoConfirmacaoPagamento.PAYMENT.name())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(mercadopagoHttpClient, never()).consultarMerchantOrder(anyLong());
    }

    @Test
    void deveriaFalharAoLancarIoException() throws Exception {
        var arrayByte = new byte[1];
        var messageError = "Error IOException";

        when(qrCodeHttpClient.gerarQrCode(anyString(), anyString()))
                .thenReturn(ResponseEntity.of(Optional.of(arrayByte)));

        try (MockedStatic<ImageIO> imageIO = Mockito.mockStatic(ImageIO.class)) {
            imageIO.when(() -> ImageIO.read(any(ByteArrayInputStream.class)))
                    .thenThrow(new IOException(messageError));

            mockMvc.perform(post(pagamentoPath + "/{idPedido}/qrcode", idPedido)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.mensagem").value(messageError));

        }

        verify(qrCodeHttpClient).gerarQrCode(anyString(), anyString());
    }

    private Optional<Pagamento> obterPagamento() {
        return pagamentoGateway.obterPagamentoPorIdPedido(this.idPedido);
    }
}