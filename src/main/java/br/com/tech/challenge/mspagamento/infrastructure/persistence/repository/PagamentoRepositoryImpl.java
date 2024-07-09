package br.com.tech.challenge.mspagamento.infrastructure.persistence.repository;

import br.com.tech.challenge.mspagamento.application.repository.PagamentoRepository;
import br.com.tech.challenge.mspagamento.core.domain.Pagamento;
import br.com.tech.challenge.mspagamento.infrastructure.mapper.PagamentoModelMapper;
import br.com.tech.challenge.mspagamento.infrastructure.persistence.repository.mongodb.PagamentoRepositoryMongoDB;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PagamentoRepositoryImpl implements PagamentoRepository {
    private final PagamentoRepositoryMongoDB pagamentoRepositoryMongoDB;
    private final PagamentoModelMapper mapper;

    @Override
    public Optional<Pagamento> obterPagamentoPorIdPedido(Long idPedido) {
        return pagamentoRepositoryMongoDB.findByPedidoId(idPedido)
                .map(mapper::toDomain);
    }

    @Override
    public Pagamento salvar(Pagamento pagamento) {
        var pagamentoModel = pagamentoRepositoryMongoDB.save(mapper.toModel(pagamento));

        return mapper.toDomain(pagamentoModel);
    }
}
