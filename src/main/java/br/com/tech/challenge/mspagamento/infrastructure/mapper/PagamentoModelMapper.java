package br.com.tech.challenge.mspagamento.infrastructure.mapper;

import br.com.tech.challenge.mspagamento.application.dto.PagamentoConfirmadoDTO;
import br.com.tech.challenge.mspagamento.core.domain.Pagamento;
import br.com.tech.challenge.mspagamento.infrastructure.persistence.model.PagamentoModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagamentoModelMapper {
    private final PedidoModelMapper pedidoMapper;

    public PagamentoModel toModel(Pagamento pagamento) {
        return PagamentoModel.builder()
                .id(pagamento.getId())
                .idPagamentoExterno(pagamento.getIdPagamentoExterno())
                .total(pagamento.getTotal())
                .status(pagamento.getStatus())
                .qrCode(pagamento.getQrCode())
                .pedido(pedidoMapper.toModel(pagamento.getPedido()))
                .build();
    }

    public Pagamento toDomain(PagamentoModel pagamento) {
        return Pagamento.builder()
                .id(pagamento.getId())
                .idPagamentoExterno(pagamento.getIdPagamentoExterno())
                .total(pagamento.getTotal())
                .status(pagamento.getStatus())
                .qrCode(pagamento.getQrCode())
                .pedido(pedidoMapper.toDomain(pagamento.getPedido()))
                .build();
    }

    public PagamentoConfirmadoDTO toPagamentoConfirmadoDTO(Pagamento pagamento) {
        return new PagamentoConfirmadoDTO(pagamento.getPedido().getId(), pagamento.getStatus());
    }
}
