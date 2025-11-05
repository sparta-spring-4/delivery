package com.zts.delivery.payment.presentation.dto;

public record TossPaymentConfirmRequest(
        String orderId,
        String paymentKey,
        int amount
) {
}
