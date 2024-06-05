package br.com.tech.challenge.mspagamento.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Pedido {
    private Integer id;
    private String status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private BigDecimal total;
    private Boolean pago;
    private String cliente;
    private List<ItemPedido> itens;

    public Pedido(PedidoBuilder pedidoBuilder) {
        this.id = pedidoBuilder.id;
        this.status = pedidoBuilder.status;
        this.dataCriacao = pedidoBuilder.dataCriacao;
        this.dataAtualizacao = pedidoBuilder.dataAtualizacao;
        this.total = pedidoBuilder.total;
        this.pago = pedidoBuilder.pago;
        this.cliente = pedidoBuilder.cliente;
        this.itens = pedidoBuilder.itens;
    }

    public static PedidoBuilder builder() {
        return new PedidoBuilder();
    }

    public Integer getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Boolean getPago() {
        return pago;
    }

    public String getCliente() {
        return cliente;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public static class PedidoBuilder {
        private Integer id;
        private String status;
        private LocalDateTime dataCriacao;
        private LocalDateTime dataAtualizacao;
        private BigDecimal total;
        private Boolean pago;
        private String cliente;
        private List<ItemPedido> itens;

        public PedidoBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public PedidoBuilder status(String status) {
            this.status = status;
            return this;
        }

        public PedidoBuilder dataCriacao(LocalDateTime dataCriacao) {
            this.dataCriacao = dataCriacao;
            return this;
        }

        public PedidoBuilder dataAtualizacao(LocalDateTime dataAtualizacao) {
            this.dataAtualizacao = dataAtualizacao;
            return this;
        }

        public PedidoBuilder total(BigDecimal total) {
            this.total = total;
            return this;
        }

        public PedidoBuilder pago(Boolean pago) {
            this.pago = pago;
            return this;
        }

        public PedidoBuilder cliente(String cliente) {
            this.cliente = cliente;
            return this;
        }

        public PedidoBuilder itens(List<ItemPedido> itens) {
            this.itens = itens;
            return this;
        }

        public Pedido build() {
            return new Pedido(this);
        }
    }
}
