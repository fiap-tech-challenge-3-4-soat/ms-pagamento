package br.com.tech.challenge.mspagamento;

import br.com.tech.challenge.mspagamento.core.domain.ItemPedido;
import br.com.tech.challenge.mspagamento.core.domain.Pagamento;
import br.com.tech.challenge.mspagamento.core.domain.Pedido;
import br.com.tech.challenge.mspagamento.core.domain.StatusPagamento;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.ConsultaMerchantOrderResponse;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.GerarCodigoQrResponse;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TestObjects {
    public static GerarCodigoQrResponse obterGerarCodigoQrResponse() {
        return new GerarCodigoQrResponse("idExternal", "qrData");
    }

    public static ConsultaMerchantOrderResponse obterConsultaMerchantOrderResponse(Long idPedido) {
        return new ConsultaMerchantOrderResponse("idExterno", "closed", false, "paid", idPedido);
    }

    public static BufferedImage obterBufferedImage() {
        return new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
    }

    public static Pagamento obterPagamento() {
        return Pagamento.builder()
                .idPagamentoExterno("idExterno")
                .total(BigDecimal.TEN)
                .status(StatusPagamento.ABERTO)
                .qrCode("qrCode")
                .pedido(obterPedido())
                .build();
    }

    public static Pedido obterPedido() {
        return Pedido.builder()
                .id(155L)
                .status("RECEBIDO")
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .total(BigDecimal.TEN)
                .pago(Boolean.FALSE)
                .cliente("Cliente Teste")
                .itens(List.of(obterItemPedido()))
                .build();
    }

    public static ItemPedido obterItemPedido() {
        return ItemPedido.builder()
                .nomeProduto("Produto Teste")
                .descricaoProduto("Descricao Produto")
                .categoriaProduto("Categoria produto")
                .preco(BigDecimal.TEN)
                .quantidade(2)
                .observacao("Observacao")
                .build();
    }
}
