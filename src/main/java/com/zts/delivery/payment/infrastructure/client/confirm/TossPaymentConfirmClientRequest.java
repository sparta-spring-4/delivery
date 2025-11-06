package com.zts.delivery.payment.infrastructure.client.confirm;

import lombok.Builder;

@Builder
public record TossPaymentConfirmClientRequest(
        String orderId,
        int amount,
        String paymentKey
) {
}
