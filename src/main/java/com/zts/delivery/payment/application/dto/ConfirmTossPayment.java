package com.zts.delivery.payment.application.dto;

import lombok.Builder;

@Builder
public record ConfirmTossPayment(
        String orderId,
        String paymentKey,
        int amount
) {
}
