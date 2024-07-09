package br.com.tech.challenge.mspagamento.application.dto;

import br.com.tech.challenge.mspagamento.core.domain.StatusPagamento;

public record PagamentoConfirmadoDTO(Long idPedido, StatusPagamento statusPagamento) {}
