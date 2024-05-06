package br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mspedido.to;

import java.math.BigDecimal;
import java.util.List;

public record PedidoTO(Long id,
                       BigDecimal total,
                       Boolean pago,
                       List<ItemPedidoTO> itens) {}
