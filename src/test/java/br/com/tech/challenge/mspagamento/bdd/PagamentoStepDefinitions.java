package br.com.tech.challenge.mspagamento.bdd;

import br.com.tech.challenge.mspagamento.TestObjects;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.GerarCodigoQrRequest;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.MercadoPagoHttpClient;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mspedido.MSPedidoHttpClient;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.qrcodeapi.QrCodeHttpClient;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@CucumberContextConfiguration
@AutoConfigureMockMvc
public class PagamentoStepDefinitions {
    private final String PAGAMENTO_PATH = "/v1/pagamentos";

    private File file;

    @Value("${assets.image}")
    private String imagePath;

    private final Long idPedido = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QrCodeHttpClient qrCodeHttpClient;

    @MockBean
    private MSPedidoHttpClient msPedidoHttpClient;

    @MockBean
    private MercadoPagoHttpClient mercadopagoHttpClient;

    ResultActions resultActions;

    @BeforeEach
    public void setUp() throws Exception {
        this.file = new File(imagePath + idPedido + ".png");
        file.createNewFile();
    }

    @Quando("solicitar geração do qrcode")
    public void registrar_uma_nova_categoria() throws Exception {
        var gerarQrCodeResponse = TestObjects.obterGerarCodigoQrResponse();
        var obterPedidoResponse = TestObjects.obterPedidoResponse();
        var obterStatusPedidoResponse = TestObjects.obterStatusPedidoResponse();
        var obterBufferedImage = TestObjects.obterBufferedImage();
        var arrayByte = new byte[1];
        this.file = new File(imagePath + idPedido + ".png");
        this.file.createNewFile();

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
                    .thenReturn(obterBufferedImage);

            resultActions = mockMvc.perform(post(PAGAMENTO_PATH + "/{idPedido}", idPedido)
                            .contentType(MediaType.APPLICATION_JSON)
                    );
        }
        var teste = "";
    }
    @Entao("deve receber status 200")
    public void deve_validar_o_pedido() throws Exception {
        resultActions.andExpect(status().isOk());
    }
    @Entao("exibir a imagem QrCode")
    public void gerar_a_imagem_qr_code() throws Exception {
        this.file.delete();
        resultActions.andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE));
    }
}
