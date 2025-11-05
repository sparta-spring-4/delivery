package com.zts.delivery.payment.infrastructure.client;

import com.zts.delivery.payment.infrastructure.config.TossPaymentKeyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(TossPaymentKeyProperties.class)
public class TossPaymentConfirmClient {

    private final TossPaymentKeyProperties properties;

    public TossPaymentConfirmClientResponse confirm(TossPaymentConfirmClientRequest request) {
        String authorizations = "Basic " + encodingToBase64(properties.secretKey());

        ResponseEntity<TossPaymentConfirmClientResponse> response = RestClient.create().post()
                .uri(properties.confirm().uri())
                .header("Authorization", authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(TossPaymentConfirmClientResponse.class);

        return response.getBody();
    }

    private String encodingToBase64(String secretKey) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes);
    }
}
