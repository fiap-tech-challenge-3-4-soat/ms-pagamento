package br.com.tech.challenge.mspagamento.infrastructure.integration.transfer;

public record IntegrationErrorTO(int status,
                                 String error,
                                 String message) {
}
