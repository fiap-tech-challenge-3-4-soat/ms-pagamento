package br.com.tech.challenge.mspagamento.core.domain;

import java.math.BigDecimal;

public class ItemPedido {
    private String nomeProduto;
    private String descricaoProduto;
    private String categoriaProduto;
    private BigDecimal preco;
    private Integer quantidade;
    private String observacao;

    public ItemPedido(ItemPedidoBuilder itemPedidoBuilder) {
        this.nomeProduto = itemPedidoBuilder.nomeProduto;
        this.descricaoProduto = itemPedidoBuilder.descricaoProduto;
        this.categoriaProduto = itemPedidoBuilder.categoriaProduto;
        this.preco = itemPedidoBuilder.preco;
        this.quantidade = itemPedidoBuilder.quantidade;
        this.observacao = itemPedidoBuilder.observacao;
    }

    public static ItemPedidoBuilder builder() {
        return new ItemPedidoBuilder();
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public String getCategoriaProduto() {
        return categoriaProduto;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public String getObservacao() {
        return observacao;
    }

    public static class ItemPedidoBuilder {
        private String nomeProduto;
        private String descricaoProduto;
        private String categoriaProduto;
        private BigDecimal preco;
        private Integer quantidade;
        private String observacao;

        public ItemPedidoBuilder nomeProduto(String nomeProduto) {
            this.nomeProduto = nomeProduto;
            return this;
        }

        public ItemPedidoBuilder descricaoProduto(String descricaoProduto) {
            this.descricaoProduto = descricaoProduto;
            return this;
        }

        public ItemPedidoBuilder categoriaProduto(String categoriaProduto) {
            this.categoriaProduto = categoriaProduto;
            return this;
        }

        public ItemPedidoBuilder preco(BigDecimal preco) {
            this.preco = preco;
            return this;
        }

        public ItemPedidoBuilder quantidade(Integer quantidade) {
            this.quantidade = quantidade;
            return this;
        }

        public ItemPedidoBuilder observacao(String observacao) {
            this.observacao = observacao;
            return this;
        }

        public ItemPedido build() {
            return new ItemPedido(this);
        }
    }
}
