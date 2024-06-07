package br.com.tech.challenge.mspagamento.infrastructure.persistence.model;

import br.com.tech.challenge.mspagamento.core.domain.StatusPagamento;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("pagamentos")
public class PagamentoModel {
    @Id
    private String id;
    @Indexed
    private Long idPedido;
    private String idPagamentoExterno;
    private BigDecimal total;
    @Enumerated(EnumType.STRING)
    private StatusPagamento status;
    private String qrCode;
    private PedidoModel pedido;
}
