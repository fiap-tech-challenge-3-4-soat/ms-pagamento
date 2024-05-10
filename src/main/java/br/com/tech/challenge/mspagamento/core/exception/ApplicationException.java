package br.com.tech.challenge.mspagamento.core.exception;

public abstract class ApplicationException extends  RuntimeException {
    protected ApplicationException(String message) {
        super(message);
    }
}
