package br.com.tech.challenge.mspagamento.application.service;

import java.io.File;

public interface QrCodeService {
    File gerarImagemQrCode(String qrCodeData, Long idPedido);
}
