package com.zts.delivery.payment.infrastructure.client.cancel;

import lombok.Builder;

@Builder
public record TossPaymentCancelClientRequest(
        String paymentKey,
        String cancelReason, // 필수
        int cancelAmount // 값이 없으면 전액 환불
) {
}
