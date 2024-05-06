package br.com.tech.challenge.mspagamento.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

@Component
public class FeignDecoderHandler implements ErrorDecoder {
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));

    @Override
    public Exception decode(String s, Response response) {
        try {
            if (HttpStatus.UNAUTHORIZED.value() == response.status()) {
                throw new DefaultFeignException(response.status(), "UNAUTHORIZED", "Unauthorized");
            }

            String inputStream = StreamUtils.copyToString(response.body().asInputStream(), StandardCharsets.UTF_8);
            return mapper.readValue(inputStream, DefaultFeignException.class);
        } catch (Exception exception) {
            throw new DecodeException("Não foi possível decodificar a exceção", exception);
        }
    }
}
