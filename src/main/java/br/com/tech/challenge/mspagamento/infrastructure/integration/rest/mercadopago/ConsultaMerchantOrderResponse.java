package br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConsultaMerchantOrderResponse(String id,
                                            String status,
                                            Boolean cancelled,
                                            @JsonProperty("order_status") String orderStatus,
                                            @JsonProperty("external_reference") Long externalReference) {

    public boolean isPaid() {
        return Boolean.FALSE.equals(cancelled)
                && status.equalsIgnoreCase("closed")
                && orderStatus.equalsIgnoreCase("paid");
    }
}
