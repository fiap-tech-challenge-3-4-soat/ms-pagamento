package br.com.tech.challenge.mspagamento.infrastructure.integration.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ItemPedidoTO {
    private String nomeProduto;
    private String descricaoProduto;
    private String categoriaProduto;
    private BigDecimal preco;
    private Integer quantidade;
    private String observacao;
}
