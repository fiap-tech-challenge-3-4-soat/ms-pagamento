package br.com.tech.challenge.mspagamento.infrastructure.mapper;

import br.com.tech.challenge.mspagamento.core.domain.Pagamento;
import br.com.tech.challenge.mspagamento.core.domain.Pedido;
import br.com.tech.challenge.mspagamento.infrastructure.integration.transfer.PedidoTO;
import br.com.tech.challenge.mspagamento.infrastructure.persistence.model.PagamentoModel;
import br.com.tech.challenge.mspagamento.infrastructure.persistence.model.PedidoModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PedidoModelMapper {
    private final ItemPedidoModelMapper itemPedidoMapper;

    public PedidoModel toModel(Pedido pedido) {
        var itens = pedido.getItens().stream()
                .map(itemPedidoMapper::toModel)
                .toList();

        return PedidoModel.builder()
                .id(pedido.getId())
                .status(pedido.getStatus())
                .dataCriacao(pedido.getDataCriacao())
                .dataAtualizacao(pedido.getDataAtualizacao())
                .total(pedido.getTotal())
                .pago(pedido.getPago())
                .cliente(pedido.getCliente())
                .itens(itens)
                .build();
    }

    public Pedido toDomain(PedidoModel pedido) {
        var itens = pedido.getItens().stream()
                .map(itemPedidoMapper::toDomain)
                .toList();

        return Pedido.builder()
                .id(pedido.getId())
                .status(pedido.getStatus())
                .dataCriacao(pedido.getDataCriacao())
                .dataAtualizacao(pedido.getDataAtualizacao())
                .total(pedido.getTotal())
                .pago(pedido.getPago())
                .cliente(pedido.getCliente())
                .itens(itens)
                .build();
    }

    public Pedido toDomain(PedidoTO pedido) {
        var itens = pedido.getItens()
                .stream()
                .map(itemPedidoMapper::toDomain)
                .toList();

        return Pedido.builder()
                .id(pedido.getId())
                .status(pedido.getStatus())
                .dataCriacao(pedido.getDataCriacao())
                .dataAtualizacao(pedido.getDataAtualizacao())
                .total(pedido.getTotal())
                .pago(pedido.getPago())
                .cliente(pedido.getCliente())
                .itens(itens)
                .build();
    }
}
