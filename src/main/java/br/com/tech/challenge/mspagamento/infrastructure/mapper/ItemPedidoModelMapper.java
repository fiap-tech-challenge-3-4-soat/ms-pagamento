package br.com.tech.challenge.mspagamento.infrastructure.mapper;

import br.com.tech.challenge.mspagamento.core.domain.ItemPedido;
import br.com.tech.challenge.mspagamento.infrastructure.integration.transfer.ItemPedidoTO;
import br.com.tech.challenge.mspagamento.infrastructure.persistence.model.ItemPedidoModel;
import br.com.tech.challenge.mspagamento.infrastructure.persistence.model.PedidoModel;
import org.springframework.stereotype.Component;

@Component
public class ItemPedidoModelMapper {
    public ItemPedido toDomain(ItemPedidoModel itemPedido) {
        return ItemPedido.builder()
                .nomeProduto(itemPedido.getNomeProduto())
                .descricaoProduto(itemPedido.getDescricaoProduto())
                .categoriaProduto(itemPedido.getCategoriaProduto())
                .preco(itemPedido.getPreco())
                .quantidade(itemPedido.getQuantidade())
                .observacao(itemPedido.getObservacao())
                .build();
    }

    public ItemPedido toDomain(ItemPedidoTO itemPedido) {
        return ItemPedido.builder()
                .nomeProduto(itemPedido.getNomeProduto())
                .descricaoProduto(itemPedido.getDescricaoProduto())
                .categoriaProduto(itemPedido.getCategoriaProduto())
                .preco(itemPedido.getPreco())
                .quantidade(itemPedido.getQuantidade())
                .observacao(itemPedido.getObservacao())
                .build();
    }

    public ItemPedidoModel toModel(ItemPedido itemPedido) {
        return ItemPedidoModel.builder()
                .nomeProduto(itemPedido.getNomeProduto())
                .descricaoProduto(itemPedido.getDescricaoProduto())
                .categoriaProduto(itemPedido.getCategoriaProduto())
                .preco(itemPedido.getPreco())
                .quantidade(itemPedido.getQuantidade())
                .observacao(itemPedido.getObservacao())
                .build();
    }
}
