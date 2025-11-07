package com.zts.delivery.payment.presentation.dto;

import java.util.UUID;

public record CancelTossPaymentRequest(
        UUID orderId,
        int cancelAmount,
        String cancelReason
) {
}
