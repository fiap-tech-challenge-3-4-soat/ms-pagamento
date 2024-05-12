package br.com.tech.challenge.mspagamento.infrastructure.integration.rest.mspedido;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "ms-pedido", url = "${rest.service.ms-pedido.url}/${rest.service.ms-pedido.context}")
public interface MSPedidoHttpClient {
    @GetMapping("/pedidos/{idPedido}")
    ResponseEntity<ObterPedidoResponse> obterPedido(@PathVariable Long idPedido);

    @GetMapping("/pedidos/{idPedido}/status")
    ResponseEntity<ObterStatusPedidoResponse> obterStatusPedido(@PathVariable Long idPedido);

    @PutMapping("/pedidos/{idPedido}/confirmar-pagamento")
    ResponseEntity<Void> confirmarPagamento(@PathVariable Long idPedido);
}
