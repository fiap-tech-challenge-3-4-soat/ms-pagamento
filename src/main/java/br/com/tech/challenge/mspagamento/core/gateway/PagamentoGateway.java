package br.com.tech.challenge.mspagamento.core.gateway;

import java.io.File;

public interface PagamentoGateway {
    File criarPagamentoPorQrCode(Long idPedido);
}
