package com.zts.delivery.payment.infrastructure.client.cancel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zts.delivery.payment.infrastructure.client.TossClientErrorException;
import com.zts.delivery.payment.infrastructure.client.TossClientErrorResponse;
import com.zts.delivery.payment.infrastructure.client.TossPaymentClientResponse;
import com.zts.delivery.payment.infrastructure.config.TossPaymentKeyProperties;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(TossPaymentKeyProperties.class)
public class TossPaymentCancelClient {

    private final TossPaymentKeyProperties properties;

    private final ObjectMapper objectMapper;

    public TossPaymentClientResponse cancel(TossPaymentCancelClientRequest cancelRequest) {
        String authorizations = "Basic " + encodingToBase64(properties.secretKey());

        String cancelUri = String.format(properties.cancel().uri(), cancelRequest.paymentKey());

        TossPaymentCancelClientBody.TossPaymentCancelClientBodyBuilder body = TossPaymentCancelClientBody.builder()
                .cancelAmount(cancelRequest.cancelAmount())
                .cancelReason(cancelRequest.cancelReason());

        ResponseEntity<TossPaymentClientResponse> confirmResponse = RestClient.create().post()
                .uri(cancelUri)
                .header("Authorization", authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    TossClientErrorResponse errorResponse = objectMapper.readValue(response.getBody(), TossClientErrorResponse.class);
                    throw new TossClientErrorException(response.getStatusCode(), errorResponse.code(), errorResponse.message());
                })
                .toEntity(TossPaymentClientResponse.class);

        return confirmResponse.getBody();
    }

    private String encodingToBase64(String secretKey) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes);
    }

    @Builder
    record TossPaymentCancelClientBody(
            String cancelReason,
            int cancelAmount
    ) {
    }
}
