package br.com.tech.challenge.mspagamento.infrastructure.exception;

public class InternalErrorException extends RuntimeException {
    public InternalErrorException(String message) {
        super(message);
    }
}
