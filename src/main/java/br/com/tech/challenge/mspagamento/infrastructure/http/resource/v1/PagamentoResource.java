package br.com.tech.challenge.mspagamento.infrastructure.http.resource.v1;

import br.com.tech.challenge.mspagamento.application.controller.PagamentoController;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mercadopago.EventoConfirmacaoPagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/pagamentos")
public class PagamentoResource {
    private final PagamentoController controller;

    @PostMapping("/confirmar-pagamento")
    public ResponseEntity<Void> receberConfirmacaoPagamento(@RequestParam(required = false) Long id,
                                                            @RequestParam(required = false) EventoConfirmacaoPagamento topic) {

        if (EventoConfirmacaoPagamento.MOCK.equals(topic) && Objects.nonNull(id)) {
            controller.pagar(id);
        }

        if (EventoConfirmacaoPagamento.MERCHANT_ORDER.equals(topic) && Objects.nonNull(id)) {
            controller.receberConfirmacaoPagamento(id);
        }

        return ResponseEntity.ok().build();
    }
}
