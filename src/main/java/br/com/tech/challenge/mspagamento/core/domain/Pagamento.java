package br.com.tech.challenge.mspagamento.core.domain;

public class Pagamento {
    private String id;
    private String idPedido;
    private String idPagamentoExterno;
    private String total;
    private String status;
    private String qrCode;

    public Pagamento(PagamentoBuilder pagamentoBuilder) {
        this.id = pagamentoBuilder.id;
        this.idPedido = pagamentoBuilder.idPedido;
        this.idPagamentoExterno = pagamentoBuilder.idPagamentoExterno;
        this.total = pagamentoBuilder.total;
        this.status = pagamentoBuilder.status;
        this.qrCode = pagamentoBuilder.qrCode;
    }

    public static PagamentoBuilder builder() {
        return new PagamentoBuilder();
    }

    public String getId() {
        return id;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public String getIdPagamentoExterno() {
        return idPagamentoExterno;
    }

    public String getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }

    public String getQrCode() {
        return qrCode;
    }

    public static class PagamentoBuilder {
        private String id;
        private String idPedido;
        private String idPagamentoExterno;
        private String total;
        private String status;
        private String qrCode;

        public PagamentoBuilder id(String id) {
            this.id = id;
            return this;
        }

        public PagamentoBuilder idPedido(String idPedido) {
            this.idPedido = idPedido;
            return this;
        }


        public PagamentoBuilder idPagamentoExterno(String idExterno) {
            this.idPagamentoExterno = idExterno;
            return this;
        }

        public PagamentoBuilder total(String total) {
            this.total = total;
            return this;
        }

        public PagamentoBuilder status(String status) {
            this.status = status;
            return this;
        }

        public PagamentoBuilder qrCode(String qrCode) {
            this.qrCode = qrCode;
            return this;
        }

        public Pagamento build() {
            return new Pagamento(this);
        }
    }
}
