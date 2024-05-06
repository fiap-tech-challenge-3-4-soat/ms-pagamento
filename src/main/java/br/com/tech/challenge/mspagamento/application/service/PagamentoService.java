package br.com.tech.challenge.mspagamento.application.service;

import java.io.File;

public interface PagamentoService {
    File gerarQrCode(Long idPedido);
    Long confirmarPagamento(Long idExterno);
}
