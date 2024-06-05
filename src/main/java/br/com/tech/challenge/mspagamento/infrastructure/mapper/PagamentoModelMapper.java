package br.com.tech.challenge.mspagamento.infrastructure.mapper;

import br.com.tech.challenge.mspagamento.core.domain.Pagamento;
import br.com.tech.challenge.mspagamento.infrastructure.persistence.model.PagamentoModel;
import org.springframework.stereotype.Component;

@Component
public class PagamentoModelMapper {
    public PagamentoModel toModel(Pagamento pagamento) {
        return PagamentoModel.builder()
                .id(pagamento.getId())
                .idPagamentoExterno(pagamento.getIdPagamentoExterno())
                .total(pagamento.getTotal())
                .status(pagamento.getStatus())
                .qrCode(pagamento.getQrCode())
                .build();
    }

    public Pagamento toDomain(PagamentoModel pagamento) {
        return Pagamento.builder()
                .id(pagamento.getId())
                .idPagamentoExterno(pagamento.getIdPagamentoExterno())
                .total(pagamento.getTotal())
                .status(pagamento.getStatus())
                .qrCode(pagamento.getQrCode())
                .build();
    }
}
