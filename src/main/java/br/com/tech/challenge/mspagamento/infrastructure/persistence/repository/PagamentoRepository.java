package br.com.tech.challenge.mspagamento.infrastructure.persistence.repository;


import br.com.tech.challenge.mspagamento.infrastructure.persistence.model.PagamentoModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagamentoRepository extends MongoRepository<PagamentoModel, String> {
}
