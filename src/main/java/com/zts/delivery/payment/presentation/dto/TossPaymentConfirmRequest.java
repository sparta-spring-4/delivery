package com.zts.delivery.payment.presentation.dto;

import java.util.UUID;

public record TossPaymentConfirmRequest(
        UUID orderId,
        String paymentKey,
        int amount
) {
}
