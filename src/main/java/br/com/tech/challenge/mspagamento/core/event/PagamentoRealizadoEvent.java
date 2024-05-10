package br.com.tech.challenge.mspagamento.core.event;

import br.com.tech.challenge.mspagamento.core.domain.Pagamento;

public record PagamentoRealizadoEvent(Pagamento pagamento) {}
