package br.com.tech.challenge.mspagamento.core.exception;

public class PagamentoInexistenteException extends ApplicationException {
    public PagamentoInexistenteException(Long idPedido) {
        super(String.format("Pagamento inexistente, id do pedido: %d", idPedido));
    }
}
