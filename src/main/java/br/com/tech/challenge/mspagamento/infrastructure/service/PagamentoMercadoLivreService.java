package br.com.tech.challenge.mspagamento.infrastructure.service;

import br.com.tech.challenge.mspagamento.application.service.PagamentoService;
import br.com.tech.challenge.mspagamento.core.domain.Pagamento;
import br.com.tech.challenge.mspagamento.infrastructure.exception.IntegrationException;
import br.com.tech.challenge.mspagamento.infrastructure.exception.InternalErrorException;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.GerarCodigoQrRequest;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.MercadoPagoHttpClient;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.to.ItemTO;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mspedido.MSPedidoHttpClient;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mspedido.to.ItemPedidoTO;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.qrcodeapi.QrCodeHttpClient;
import br.com.tech.challenge.mspagamento.infrastructure.mapper.PagamentoModelMapper;
import br.com.tech.challenge.mspagamento.infrastructure.persistence.repository.mongodb.PagamentoRepositoryMongoDB;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PagamentoMercadoLivreService implements PagamentoService {
    private final MercadoPagoHttpClient mercadopagoHttpClient;
    private final QrCodeHttpClient qrCodeHttpClient;
    private final MSPedidoHttpClient msPedidoHttpClient;
    private final PagamentoRepositoryMongoDB pagamentoRepositoryMongoDB;
    private final PagamentoModelMapper mapper;

    private final Long HORAS_ADICIONAIS = 2L;

    @Value("${assets.image}")
    private String defaultPath;

    @Value("${rest.service.mercadopago.notification-url}")
    private String notificacaoUrl;

    @Override
    public File gerarQrCode(Pagamento pagamento) {
        try {
            var request = obterMercadoPagoRequest(pagamento.getIdPedido());
            var gerarQrDataResponse = mercadopagoHttpClient.gerarQrData(request);
            var qrCodeData = gerarQrDataResponse.getBody();

            if (Objects.isNull(qrCodeData) || Objects.isNull(qrCodeData.qrData())) {
                throw new IntegrationException("Não foi possível obter os dados para gerar o QR-Code");
            }

            var gerarQrCodeResponse = qrCodeHttpClient.gerarQrCode("300x300", qrCodeData.qrData());
            var qrCodeImage = gerarQrCodeResponse.getBody();

            if (Objects.isNull(qrCodeImage)) {
                throw new IntegrationException("Não foi possível obter a imagem do QR-Code");
            }

            ByteArrayInputStream bis = new ByteArrayInputStream(qrCodeImage);
            BufferedImage bufferedImage = ImageIO.read(bis);
            String fileName = defaultPath + pagamento.getIdPedido() + ".png";
            var file = new File(fileName);
            ImageIO.write(bufferedImage, "png", file);

            pagamento.adicionarIdExterno(qrCodeData.inStoreOrderid());
            pagamento.adicionarTotal(request.getTotalAmount());
            pagamento.adicionarQrCode(qrCodeData.qrData());

            pagamentoRepositoryMongoDB.save(mapper.toModel(pagamento));

            return file;
        } catch (IOException e) {
            throw new InternalErrorException(e.getMessage());
        } catch (FeignException exception) {
            throw new IntegrationException(exception.getMessage());
        }
    }

    @Override
    public Long confirmarPagamento(Long idExterno) {
        try {
            var response = mercadopagoHttpClient.consultarMerchantOrder(idExterno);
            var consultaPagamentoResponse = response.getBody();

            if (Objects.nonNull(consultaPagamentoResponse) && consultaPagamentoResponse.isPaid()) {
                return consultaPagamentoResponse.externalReference();
            }

            return null;
        } catch (FeignException exception) {
            throw new IntegrationException(exception.getMessage());
        }
    }

    private GerarCodigoQrRequest obterMercadoPagoRequest(Long idPedido) {
        var response = msPedidoHttpClient.obterPedido(idPedido);
        var pedido = response.getBody();
        var items = this.obterItems(pedido.pedido().itens());

        ZoneId zone = ZoneId.of("America/Sao_Paulo");
        ZonedDateTime expiration = new Date().toInstant()
                .atZone(zone)
                .toLocalDateTime().plusHours(HORAS_ADICIONAIS).atZone(zone);

        return GerarCodigoQrRequest.builder()
                .description("Pedido para pagamento")
                .externalReference(String.valueOf(pedido.pedido().id()))
                .expirationDate(expiration)
                .notificationUrl(notificacaoUrl)
                .title("Restaurante Fiap")
                .totalAmount(pedido.pedido().total())
                .items(items)
                .build();
    }

    private List<ItemTO> obterItems(List<ItemPedidoTO> itens) {
        return itens.stream()
                .map(item -> ItemTO.builder()
                        .title(item.nomeProduto())
                        .description(item.descricaoProduto())
                        .unitPrice(item.preco())
                        .quantity(item.quantidade())
                        .unitMeasure("unit")
                        .totalAmount(item.preco().multiply(new BigDecimal(item.quantidade())))
                        .build())
                .toList();
    }
}
