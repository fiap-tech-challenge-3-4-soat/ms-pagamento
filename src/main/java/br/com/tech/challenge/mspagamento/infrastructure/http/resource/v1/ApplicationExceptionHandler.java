package br.com.tech.challenge.mspagamento.infrastructure.http.resource.v1;

import br.com.tech.challenge.mspagamento.application.dto.ErrorDTO;
import br.com.tech.challenge.mspagamento.infrastructure.exception.DefaultFeignException;
import br.com.tech.challenge.mspagamento.infrastructure.exception.IntegrationException;
import br.com.tech.challenge.mspagamento.infrastructure.integration.transfer.IntegrationErrorTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationExceptionHandler {
    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<ErrorDTO> handleIntegrationExceptionn(IntegrationException exception) {
        return new ResponseEntity<>(new ErrorDTO(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DefaultFeignException.class)
    public ResponseEntity<IntegrationErrorTO> handleDefaultFeign(DefaultFeignException exception) {
        return new ResponseEntity<>(new IntegrationErrorTO(exception.getStatus(), exception.getError(), exception.getMessage()),
                HttpStatus.valueOf(exception.getStatus()));
    }

    //TODO add decode exception
}
