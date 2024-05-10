package br.com.tech.challenge.mspagamento.application.service;

public interface PedidoService {
    void validarPedido(Long idPedido);
    void definirPedidoComoPago(Long idPedido);
}
