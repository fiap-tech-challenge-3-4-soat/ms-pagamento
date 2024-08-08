package br.com.tech.challenge.mspagamento.infrastructure.service;

import br.com.tech.challenge.mspagamento.application.service.PagamentoService;
import br.com.tech.challenge.mspagamento.core.domain.ItemPedido;
import br.com.tech.challenge.mspagamento.core.domain.Pagamento;
import br.com.tech.challenge.mspagamento.infrastructure.exception.IntegrationException;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.GerarCodigoQrRequest;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.MercadoPagoHttpClient;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.to.ItemTO;
import br.com.tech.challenge.mspagamento.infrastructure.mapper.PagamentoModelMapper;
import br.com.tech.challenge.mspagamento.infrastructure.persistence.repository.mongodb.PagamentoRepositoryMongoDB;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    private final PagamentoRepositoryMongoDB pagamentoRepositoryMongoDB;
    private final PagamentoModelMapper mapper;

    @Value("${rest.service.mercadopago.notification-url}")
    private String notificacaoUrl;

    @Override
    public Pagamento gerarDadosQrCode(Pagamento pagamento) {
        try {
            var request = obterMercadoPagoRequest(pagamento);
            var gerarQrDataResponse = mercadopagoHttpClient.gerarQrData(request);
            var qrCodeData = gerarQrDataResponse.getBody();

            if (Objects.isNull(qrCodeData) || Objects.isNull(qrCodeData.qrData())) {
                throw new IntegrationException("Não foi possível obter os dados para gerar o QR-Code");
            }

            pagamento.adicionarIdExterno(qrCodeData.inStoreOrderid());
            pagamento.adicionarQrCode(qrCodeData.qrData());

            var pagamentoModel = pagamentoRepositoryMongoDB.save(mapper.toModel(pagamento));

            return mapper.toDomain(pagamentoModel);
        } catch (FeignException exception) {
            throw new IntegrationException("Não foi possível gerar os dados de pagamento");
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
            throw new IntegrationException("Não foi possível consultar os dados do pedido");
        }
    }

    private GerarCodigoQrRequest obterMercadoPagoRequest(Pagamento pagamento) {
        var pedido = pagamento.getPedido();
        var items = this.obterItems(pedido.getItens());
        var horasAdicionais = 3L;

        ZoneId zone = ZoneId.of("America/Sao_Paulo");
        ZonedDateTime expiration = new Date().toInstant()
                .atZone(zone)
                .toLocalDateTime().plusHours(horasAdicionais).atZone(zone);

        return GerarCodigoQrRequest.builder()
                .description("Pedido para pagamento")
                .externalReference(String.valueOf(pedido.getId()))
                .expirationDate(expiration)
                .notificationUrl(notificacaoUrl)
                .title("Restaurante Fiap")
                .totalAmount(pagamento.getTotal())
                .items(items)
                .build();
    }

    private List<ItemTO> obterItems(List<ItemPedido> itens) {
        return itens.stream()
                .map(item -> ItemTO.builder()
                        .title(item.getNomeProduto())
                        .description(item.getDescricaoProduto())
                        .unitPrice(item.getPreco())
                        .quantity(item.getQuantidade())
                        .unitMeasure("unit")
                        .totalAmount(item.getPreco().multiply(new BigDecimal(item.getQuantidade())))
                        .build())
                .toList();
    }
}
