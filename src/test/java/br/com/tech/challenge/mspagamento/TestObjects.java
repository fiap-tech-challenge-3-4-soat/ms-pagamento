package br.com.tech.challenge.mspagamento;

import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.GerarCodigoQrResponse;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mspedido.ObterPedidoResponse;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mspedido.ObterStatusPedidoResponse;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mspedido.to.ItemPedidoTO;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mspedido.to.PedidoTO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TestObjects {
    public static GerarCodigoQrResponse obterGerarCodigoQrResponse() {
        return new GerarCodigoQrResponse("idExternal", "qrData");
    }

    public static ObterStatusPedidoResponse obterStatusPedidoResponse() {
        return new ObterStatusPedidoResponse(Boolean.FALSE);
    }

    public static ObterPedidoResponse obterPedidoResponse() {
        var itemPedido = new ItemPedidoTO("Nome teste", "Descrição Teste", "Categoria", BigDecimal.TEN, 2, "Observacao Teste");
        return new ObterPedidoResponse(new PedidoTO(1L, BigDecimal.TEN, Boolean.TRUE, new ArrayList<>(List.of(itemPedido))));
    }

    public static  BufferedImage obterBufferedImage() throws IOException {
        return new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
    }
}
