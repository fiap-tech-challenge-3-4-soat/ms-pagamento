package br.com.tech.challenge.mspagamento.core.domain;

import java.math.BigDecimal;

public class Pagamento {
    private String id;
    private String idPagamentoExterno;
    private BigDecimal total;
    private StatusPagamento status;
    private String qrCode;
    private Pedido pedido;

    public Pagamento(PagamentoBuilder pagamentoBuilder) {
        this.id = pagamentoBuilder.id;
        this.idPagamentoExterno = pagamentoBuilder.idPagamentoExterno;
        this.total = pagamentoBuilder.total;
        this.status = pagamentoBuilder.status;
        this.qrCode = pagamentoBuilder.qrCode;
        this.pedido = pagamentoBuilder.pedido;
    }

    public static PagamentoBuilder builder() {
        return new PagamentoBuilder();
    }

    public String getId() {
        return id;
    }

    public String getIdPagamentoExterno() {
        return idPagamentoExterno;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public String getQrCode() {
        return qrCode;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void adicionarIdExterno(String idExterno) {
        this.idPagamentoExterno = idExterno;
    }

    public void adicionarTotal(BigDecimal valor) {
        this.total = this.total.add(valor);
    }

    public void definirPago() {
        this.status = StatusPagamento.PAGO;
    }

    public void adicionarQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public boolean estaPago() {
        return this.status == StatusPagamento.PAGO;
    }

    public static class PagamentoBuilder {
        private String id;
        private String idPagamentoExterno;
        private BigDecimal total;
        private StatusPagamento status;
        private String qrCode;
        private Pedido pedido;

        public PagamentoBuilder id(String id) {
            this.id = id;
            return this;
        }

        public PagamentoBuilder idPagamentoExterno(String idExterno) {
            this.idPagamentoExterno = idExterno;
            return this;
        }

        public PagamentoBuilder total(BigDecimal total) {
            this.total = total;
            return this;
        }

        public PagamentoBuilder status(StatusPagamento status) {
            this.status = status;
            return this;
        }

        public PagamentoBuilder qrCode(String qrCode) {
            this.qrCode = qrCode;
            return this;
        }

        public PagamentoBuilder pedido(Pedido pedido) {
            this.pedido = pedido;
            return this;
        }

        public Pagamento build() {
            return new Pagamento(this);
        }
    }
}
