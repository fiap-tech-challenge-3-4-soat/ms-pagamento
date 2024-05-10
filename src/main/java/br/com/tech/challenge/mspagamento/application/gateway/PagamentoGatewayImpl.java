package br.com.tech.challenge.mspagamento.application.gateway;

import br.com.tech.challenge.mspagamento.application.repository.PagamentoRepository;
import br.com.tech.challenge.mspagamento.application.service.PagamentoService;
import br.com.tech.challenge.mspagamento.core.domain.Pagamento;
import br.com.tech.challenge.mspagamento.core.gateway.PagamentoGateway;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.Optional;

@Named
@RequiredArgsConstructor
public class PagamentoGatewayImpl implements PagamentoGateway {
    private final PagamentoService pagamentoService;
    private final PagamentoRepository pagamentoRepository;

    @Override
    public File gerarQrCode(Pagamento pagamento) {
        return pagamentoService.gerarQrCode(pagamento);
    }

    @Override
    public Optional<Pagamento> obterPagamentoPorIdPedido(Long id) {
        return pagamentoRepository.obterPagamentoPorIdPedido(id);
    }

    @Override
    public Pagamento salvar(Pagamento pagamento) {
        return pagamentoRepository.salvar(pagamento);
    }
}
