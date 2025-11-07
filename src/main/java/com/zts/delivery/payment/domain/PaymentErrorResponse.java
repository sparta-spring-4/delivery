package com.zts.delivery.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Builder
@Embeddable
public record PaymentErrorResponse(
        @Enumerated(EnumType.STRING)
        @Column(length = 45)
        HttpStatus httpStatus,

        @Column(length = 100)
        String errorCode,

        @Column(length = 255)
        String errorMessage,

        LocalDateTime erroredAt
) {
}
