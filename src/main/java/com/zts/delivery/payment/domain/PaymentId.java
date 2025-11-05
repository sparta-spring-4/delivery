package com.zts.delivery.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record PaymentId(
        @Column(length = 45, name="payment_id")
        UUID id
) {
}
