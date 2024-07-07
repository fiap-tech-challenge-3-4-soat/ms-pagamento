package br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConsultaMerchantOrderResponse(String id,
                                            String status,
                                            Boolean cancelled,
                                            @JsonProperty("order_status") String orderStatus,
                                            @JsonProperty("external_reference") Long externalReference) {

    public boolean isPaid() {
        return isClosed()
                && !isCancelled()
                && orderStatus.equalsIgnoreCase("paid");
    }

    public boolean isClosed() {
        return status.equalsIgnoreCase("closed");
    }

    public boolean isCancelled() {
        return Boolean.TRUE.equals(cancelled);
    }
}
