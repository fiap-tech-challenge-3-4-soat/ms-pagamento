package br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GerarCodigoQrResponse(@JsonProperty("in_store_order_id") String inStoreOrderid,
                                    @JsonProperty("qr_data") String qrData) {
}
