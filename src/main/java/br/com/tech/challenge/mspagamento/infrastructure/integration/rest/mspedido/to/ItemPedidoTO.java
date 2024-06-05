package br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mspedido.to;

import java.math.BigDecimal;

public record ItemPedidoTO(String nomeProduto,
                           String descricaoProduto,
                           String categoria,
                           BigDecimal preco,
                           Integer quantidade,
                           String observacao) {}
