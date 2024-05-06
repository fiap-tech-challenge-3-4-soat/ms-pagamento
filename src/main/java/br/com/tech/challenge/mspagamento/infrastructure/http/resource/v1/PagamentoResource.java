package br.com.tech.challenge.mspagamento.infrastructure.http.resource.v1;

import br.com.tech.challenge.mspagamento.application.controller.PagamentoController;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/pagamentos")
public class PagamentoResource {
    private final PagamentoController controller;

    @PostMapping("{idPedido}")
    public ResponseEntity<ByteArrayResource> gerarPagamento(@PathVariable Long idPedido) throws IOException {
        var arquivo = controller.gerarPagamento(idPedido);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mercadopago.png");
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(arquivo.length())
                .contentType(MediaType.IMAGE_PNG)
                .body(new ByteArrayResource(Files.readAllBytes(arquivo.toPath())));
    }
}
