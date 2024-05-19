package br.com.tech.challenge.mspagamento.infrastructure.persistence.repository.mongodb;


import br.com.tech.challenge.mspagamento.infrastructure.persistence.model.PagamentoModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PagamentoRepositoryMongoDB extends MongoRepository<PagamentoModel, String> {
    Optional<PagamentoModel> findByIdPedido(Long id);
}
