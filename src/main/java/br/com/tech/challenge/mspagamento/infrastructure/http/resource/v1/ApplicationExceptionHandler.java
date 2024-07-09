package br.com.tech.challenge.mspagamento.infrastructure.http.resource.v1;

import br.com.tech.challenge.mspagamento.application.dto.ErrorDTO;
import br.com.tech.challenge.mspagamento.core.exception.ApplicationException;
import br.com.tech.challenge.mspagamento.infrastructure.exception.IntegrationException;
import br.com.tech.challenge.mspagamento.infrastructure.exception.InternalErrorException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
public class ApplicationExceptionHandler {
    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<ErrorDTO> handleIntegrationException(IntegrationException exception) {
        log.error(exception);
        return new ResponseEntity<>(new ErrorDTO(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InternalErrorException.class)
    public ResponseEntity<ErrorDTO> handleIntegrationException(InternalErrorException exception) {
        log.error(exception);
        return new ResponseEntity<>(new ErrorDTO(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorDTO> handleApplicationException(ApplicationException exception) {
        log.warn(exception);
        return new ResponseEntity<>(new ErrorDTO(exception.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
