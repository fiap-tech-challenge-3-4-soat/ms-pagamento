package br.com.tech.challenge.mspagamento.infrastructure.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class ItemPedidoModel {
    private String nomeProduto;
    private String descricaoProduto;
    private String categoriaProduto;
    private BigDecimal preco;
    private Integer quantidade;
    private String observacao;
}
