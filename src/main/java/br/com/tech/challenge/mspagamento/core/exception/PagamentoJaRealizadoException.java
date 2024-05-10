package br.com.tech.challenge.mspagamento.core.exception;

public class PagamentoJaRealizadoException extends ApplicationException {
    public PagamentoJaRealizadoException(Long idPedido) {
        super(String.format("Pedido já está pago, id do pedido: %d", idPedido));
    }
}
