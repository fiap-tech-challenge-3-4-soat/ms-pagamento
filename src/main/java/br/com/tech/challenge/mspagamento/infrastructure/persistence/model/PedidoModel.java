package br.com.tech.challenge.mspagamento.infrastructure.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PedidoModel {
    private Long id;
    private String status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private BigDecimal total;
    private Boolean pago;
    private String cliente;
    private List<ItemPedidoModel> itens;
}
