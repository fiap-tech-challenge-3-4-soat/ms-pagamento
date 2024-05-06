package br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.to;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ItemTO {
    private String title;

    private String description;

    @JsonProperty("unit_price")
    private BigDecimal unitPrice;

    private Integer quantity;

    @JsonProperty("unit_measure")
    private String unitMeasure;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
}
