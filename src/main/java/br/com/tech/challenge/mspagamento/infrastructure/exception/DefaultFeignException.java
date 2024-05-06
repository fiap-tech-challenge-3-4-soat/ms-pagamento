package br.com.tech.challenge.mspagamento.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DefaultFeignException extends RuntimeException {
    private final int status;
    private final String error;
    private final String message;
}
