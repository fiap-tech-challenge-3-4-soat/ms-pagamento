package br.com.tech.challenge.mspagamento.infrastructure.integration.rest.qrcodeapi;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "qrcodeapi", url = "https://api.qrserver.com/v1/create-qr-code")
public interface QrCodeHttpClient {
    @GetMapping
    ResponseEntity<byte[]> gerarQrCode(@RequestParam("size") String size,
                                       @RequestParam("data") String data);
}
