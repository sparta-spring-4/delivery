package com.zts.delivery.payment.infrastructure.client;

import lombok.Builder;

@Builder
public record TossPaymentConfirmClientRequest(
        String orderId,
        int amount,
        String paymentKey
) {
}
