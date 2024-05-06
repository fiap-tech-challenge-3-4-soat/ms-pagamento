package br.com.tech.challenge.mspagamento.infrastructure.exception;

public class DecodeException extends RuntimeException {
    public DecodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
