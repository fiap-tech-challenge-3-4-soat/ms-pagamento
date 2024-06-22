package br.com.tech.challenge.mspagamento.infrastructure.integration.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PedidoTO {
    private Long id;
    private String status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private BigDecimal total;
    private Boolean pago;
    private String cliente;
    private List<ItemPedidoTO> itens;
}
