package com.zts.delivery.payment.infrastructure.client;

public record TossClientErrorResponse(
        String code,
        String message
) {
}
