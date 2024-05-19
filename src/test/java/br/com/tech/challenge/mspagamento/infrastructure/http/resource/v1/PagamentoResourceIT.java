package br.com.tech.challenge.mspagamento.infrastructure.http.resource.v1;

import br.com.tech.challenge.mspagamento.application.service.PedidoService;
import br.com.tech.challenge.mspagamento.core.domain.Pagamento;
import br.com.tech.challenge.mspagamento.core.domain.StatusPagamento;
import br.com.tech.challenge.mspagamento.core.gateway.PagamentoGateway;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.*;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mspedido.MSPedidoHttpClient;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mspedido.ObterPedidoResponse;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mspedido.ObterStatusPedidoResponse;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mspedido.to.ItemPedidoTO;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mspedido.to.PedidoTO;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.qrcodeapi.QrCodeHttpClient;
import br.com.tech.challenge.mspagamento.infrastructure.persistence.repository.mongodb.PagamentoRepositoryMongoDB;
import feign.FeignException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final String PAGAMENTO_PATH = "/v1/pagamentos";

    @Mock
    private FeignException feignException;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    @MockBean
    private MercadoPagoHttpClient mercadopagoHttpClient;

    @MockBean
    private QrCodeHttpClient qrCodeHttpClient;

    @MockBean
    private MSPedidoHttpClient msPedidoHttpClient;

    @Autowired
    private PagamentoRepositoryMongoDB pagamentoRepositoryMongoDB;

    @Autowired
    private PagamentoGateway pagamentoGateway;

    private File file;

    @Value("${assets.image}")
    private String imagePath;

    private final Long idPedido = 1L;

    @BeforeEach
    public void setUp() throws Exception {
        this.file = new File(imagePath + idPedido + ".png");
        file.createNewFile();
    }

    @AfterEach
    public void tearDown() {
        this.file.delete();
        pagamentoRepositoryMongoDB.deleteAll();
    }

    @Test
    void deveriaGerarQrCodeDePagamentoComSucesso() throws Exception {
        var gerarQrCodeResponse = obterGerarCodigoQrResponse();
        var obterPedidoResponse = obterPedidoResponse();
        var obterStatusPedidoResponse = obterStatusPedidoResponse();
        var arrayByte = new byte[1];

        when(msPedidoHttpClient.obterStatusPedido(idPedido))
                .thenReturn(ResponseEntity.of(Optional.of(obterStatusPedidoResponse)));
        when(msPedidoHttpClient.obterPedido(idPedido))
                .thenReturn(ResponseEntity.of(Optional.of(obterPedidoResponse)));
        when(mercadopagoHttpClient.gerarQrData(any(GerarCodigoQrRequest.class)))
                .thenReturn(ResponseEntity.of(Optional.of(gerarQrCodeResponse)));
        when(qrCodeHttpClient.gerarQrCode(anyString(), anyString()))
                .thenReturn(ResponseEntity.of(Optional.of(arrayByte)));

        try (MockedStatic<ImageIO> imageIO = Mockito.mockStatic(ImageIO.class)) {
            imageIO.when(() -> ImageIO.read(any(ByteArrayInputStream.class)))
                    .thenReturn(obterBufferedImage());

            mockMvc.perform(post(PAGAMENTO_PATH + "/{idPedido}", idPedido)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE));
        }

        var pagamentoOptional = obterPagamento();

        verify(msPedidoHttpClient).obterPedido(idPedido);
        verify(mercadopagoHttpClient).gerarQrData(any(GerarCodigoQrRequest.class));
        verify(qrCodeHttpClient).gerarQrCode(anyString(), anyString());

        assertTrue(pagamentoOptional.isPresent());
    }

    @Test
    void deveriaFalharQuandoARespostaParaGerarOQrCodeForInvalido() throws Exception {
        var obterPedidoResponse = obterPedidoResponse();

        when(msPedidoHttpClient.obterPedido(idPedido))
                .thenReturn(ResponseEntity.of(Optional.of(obterPedidoResponse)));
        when(mercadopagoHttpClient.gerarQrData(any(GerarCodigoQrRequest.class)))
                .thenReturn(ResponseEntity.of(Optional.empty()));

        mockMvc.perform(post(PAGAMENTO_PATH + "/{idPedido}", idPedido)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensagem").value("Não foi possível obter os dados para gerar o QR-Code"));

        var pagamentoOptional = obterPagamento();

        verify(msPedidoHttpClient).obterPedido(idPedido);
        verify(mercadopagoHttpClient).gerarQrData(any(GerarCodigoQrRequest.class));
        verify(qrCodeHttpClient, never()).gerarQrCode(anyString(), anyString());

        assertFalse(pagamentoOptional.isPresent());
    }
    @Test
    void deveriaFalharQuandoARespostaParaGerarAImagemDoQrCodeForInvalido() throws Exception {
        var gerarQrCodeResponse = obterGerarCodigoQrResponse();
        var obterPedidoResponse = obterPedidoResponse();
        var obterStatusPedidoResponse = obterStatusPedidoResponse();

        when(msPedidoHttpClient.obterStatusPedido(idPedido))
                .thenReturn(ResponseEntity.of(Optional.of(obterStatusPedidoResponse)));
        when(msPedidoHttpClient.obterPedido(idPedido))
                .thenReturn(ResponseEntity.of(Optional.of(obterPedidoResponse)));
        when(mercadopagoHttpClient.gerarQrData(any(GerarCodigoQrRequest.class)))
                .thenReturn(ResponseEntity.of(Optional.of(gerarQrCodeResponse)));
        when(qrCodeHttpClient.gerarQrCode(anyString(), anyString()))
                .thenReturn(ResponseEntity.of(Optional.empty()));

        mockMvc.perform(post(PAGAMENTO_PATH + "/{idPedido}", idPedido)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensagem").value("Não foi possível obter a imagem do QR-Code"));

        var pagamentoOptional = obterPagamento();

        verify(msPedidoHttpClient).obterPedido(idPedido);
        verify(mercadopagoHttpClient).gerarQrData(any(GerarCodigoQrRequest.class));
        verify(qrCodeHttpClient).gerarQrCode(anyString(), anyString());

        assertFalse(pagamentoOptional.isPresent());
    }

    @Test
    void deveriaReceberConfirmacaoDePagamentoQuandoOTopicForMockComSucesso() throws Exception {
        var pagamento = salvarNovoPagamento();

        assertFalse(pagamento.estaPago());

        mockMvc.perform(post(PAGAMENTO_PATH + "/confirmar-pagamento")
                        .param("id", String.valueOf(idPedido))
                        .param("topic", EventoConfirmacaoPagamento.MOCK.name())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        pagamento = pagamentoGateway.obterPagamentoPorIdPedido(idPedido).get();

        assertTrue(pagamento.estaPago());
        verify(pedidoService).definirPedidoComoPago(idPedido);
    }

    @Test
    void deveriaFalharQuandoReceberConfirmacaoDePagamentoComIdPedidoInvalido() throws Exception {
        mockMvc.perform(post(PAGAMENTO_PATH + "/confirmar-pagamento")
                        .param("id", String.valueOf(idPedido))
                        .param("topic", EventoConfirmacaoPagamento.MOCK.name())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensagem").value(String.format("Pagamento inexistente, id do pedido: %s", idPedido)));


        var pagamentoOptional = pagamentoGateway.obterPagamentoPorIdPedido(idPedido);

        assertFalse(pagamentoOptional.isPresent());
        verify(pedidoService, never()).definirPedidoComoPago(idPedido);
    }

    @Test
    void deveriaReceberConfirmacaoDePagamentoComSucesso() throws Exception {
        var consultaMerchantOrderResponse = new ConsultaMerchantOrderResponse("idExterno", "closed", false, "paid", idPedido);
        var pagamento = salvarNovoPagamento();

        assertFalse(pagamento.estaPago());

        when(mercadopagoHttpClient.consultarMerchantOrder(anyLong()))
                .thenReturn(ResponseEntity.of(Optional.of(consultaMerchantOrderResponse)));

        mockMvc.perform(post(PAGAMENTO_PATH + "/confirmar-pagamento")
                        .param("id", String.valueOf(111L))
                        .param("topic", EventoConfirmacaoPagamento.MERCHANT_ORDER.name())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        pagamento = pagamentoGateway.obterPagamentoPorIdPedido(idPedido).get();

        assertTrue(pagamento.estaPago());

        verify(pedidoService).definirPedidoComoPago(idPedido);
        verify(mercadopagoHttpClient).consultarMerchantOrder(anyLong());
    }

    @Test
    void deveriaRetornarQuandoTopicForPayment() throws Exception {
        mockMvc.perform(post(PAGAMENTO_PATH + "/confirmar-pagamento")
                        .param("id", String.valueOf(111L))
                        .param("topic", EventoConfirmacaoPagamento.PAYMENT.name())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(pedidoService, never()).definirPedidoComoPago(idPedido);
        verify(mercadopagoHttpClient, never()).consultarMerchantOrder(anyLong());
    }

    @Test
    void deveriaFalharAoLancarIoException() throws Exception {
        var gerarQrCodeResponse = obterGerarCodigoQrResponse();
        var obterPedidoResponse = obterPedidoResponse();
        var obterStatusPedidoResponse = obterStatusPedidoResponse();
        var arrayByte = new byte[1];
        var messageError = "Error IOException";

        when(msPedidoHttpClient.obterStatusPedido(idPedido))
                .thenReturn(ResponseEntity.of(Optional.of(obterStatusPedidoResponse)));
        when(msPedidoHttpClient.obterPedido(idPedido))
                .thenReturn(ResponseEntity.of(Optional.of(obterPedidoResponse)));
        when(mercadopagoHttpClient.gerarQrData(any(GerarCodigoQrRequest.class)))
                .thenReturn(ResponseEntity.of(Optional.of(gerarQrCodeResponse)));
        when(qrCodeHttpClient.gerarQrCode(anyString(), anyString()))
                .thenReturn(ResponseEntity.of(Optional.of(arrayByte)));

        try (MockedStatic<ImageIO> imageIO = Mockito.mockStatic(ImageIO.class)) {
            imageIO.when(() -> ImageIO.read(any(ByteArrayInputStream.class)))
                    .thenThrow(new IOException(messageError));

            mockMvc.perform(post(PAGAMENTO_PATH + "/{idPedido}", idPedido)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.mensagem").value(messageError));

        }

        verify(msPedidoHttpClient).obterPedido(idPedido);
        verify(mercadopagoHttpClient).gerarQrData(any(GerarCodigoQrRequest.class));
        verify(qrCodeHttpClient).gerarQrCode(anyString(), anyString());
    }

    @Test
    void deveriaFalharQuandoFeignExceptionForLancadaNaCriacaoDosDadosDoQrCode() throws Exception {
        var obterPedidoResponse = obterPedidoResponse();
        var obterStatusPedidoResponse = obterStatusPedidoResponse();
        var arrayByte = new byte[1];
        var messageError = "Request Error";

        when(msPedidoHttpClient.obterStatusPedido(idPedido))
                .thenReturn(ResponseEntity.of(Optional.of(obterStatusPedidoResponse)));
        when(msPedidoHttpClient.obterPedido(idPedido))
                .thenReturn(ResponseEntity.of(Optional.of(obterPedidoResponse)));
        when(mercadopagoHttpClient.gerarQrData(any(GerarCodigoQrRequest.class)))
                .thenThrow(feignException);
        when(qrCodeHttpClient.gerarQrCode(anyString(), anyString()))
                .thenReturn(ResponseEntity.of(Optional.of(arrayByte)));
        when(feignException.getMessage())
                .thenReturn(messageError);

        mockMvc.perform(post(PAGAMENTO_PATH + "/{idPedido}", idPedido)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensagem").value(messageError));

        verify(msPedidoHttpClient).obterPedido(idPedido);
        verify(mercadopagoHttpClient).gerarQrData(any(GerarCodigoQrRequest.class));
        verify(qrCodeHttpClient, never()).gerarQrCode(anyString(), anyString());
    }

    private GerarCodigoQrResponse obterGerarCodigoQrResponse() {
        return new GerarCodigoQrResponse("idExternal", "qrData");
    }

    private ObterStatusPedidoResponse obterStatusPedidoResponse() {
        return new ObterStatusPedidoResponse(Boolean.TRUE);
    }

    private ObterPedidoResponse obterPedidoResponse() {
        var itemPedido = new ItemPedidoTO("Nome teste", "Descrição Teste", BigDecimal.TEN, 2, "Observacao Teste");
        return new ObterPedidoResponse(new PedidoTO(1L, BigDecimal.TEN, Boolean.TRUE, new ArrayList<>(List.of(itemPedido))));
    }

    private BufferedImage obterBufferedImage() throws IOException {
        return new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
    }

    private Optional<Pagamento> obterPagamento() {
        return pagamentoGateway.obterPagamentoPorIdPedido(this.idPedido);
    }

    private Pagamento salvarNovoPagamento() {
        var pagamento = Pagamento.builder()
                .idPedido(idPedido)
                .status(StatusPagamento.ABERTO)
                .build();
        return pagamentoGateway.salvar(pagamento);
    }
}