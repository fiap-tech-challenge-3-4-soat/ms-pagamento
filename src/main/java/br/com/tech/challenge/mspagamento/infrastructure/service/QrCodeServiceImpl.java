package br.com.tech.challenge.mspagamento.infrastructure.service;

import br.com.tech.challenge.mspagamento.application.service.QrCodeService;
import br.com.tech.challenge.mspagamento.infrastructure.exception.IntegrationException;
import br.com.tech.challenge.mspagamento.infrastructure.exception.InternalErrorException;
import br.com.tech.challenge.mspagamento.infrastructure.integration.rest.qrcodeapi.QrCodeHttpClient;
import feign.FeignException;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Named
@RequiredArgsConstructor
public class QrCodeServiceImpl implements QrCodeService {
    private final QrCodeHttpClient qrCodeHttpClient;

    @Value("${assets.image}")
    private String defaultPath;

    @Override
    public File gerarImagemQrCode(String qrCodeData, Long idPedido) {
        try {
            var gerarQrCodeResponse = qrCodeHttpClient.gerarQrCode("300x300", qrCodeData);
            var qrCodeImage = gerarQrCodeResponse.getBody();

            if (Objects.isNull(qrCodeImage)) {
                throw new IntegrationException("Não foi possível obter a imagem do QR-Code");
            }

            ByteArrayInputStream bis = new ByteArrayInputStream(qrCodeImage);
            BufferedImage bufferedImage = ImageIO.read(bis);
            String fileName = defaultPath + idPedido + ".png";
            var file = new File(fileName);
            ImageIO.write(bufferedImage, "png", file);
            return file;
        } catch (IOException e) {
            throw new InternalErrorException(e.getMessage());
        } catch (FeignException exception) {
            throw new IntegrationException(exception.getMessage());
        }
    }
}
