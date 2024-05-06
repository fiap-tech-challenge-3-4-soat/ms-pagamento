package br.com.tech.challenge.mspagamento.infrastructure.exception;

public class IntegrationException extends RuntimeException {
    public IntegrationException(String message) {
        super(message);
    }
}
