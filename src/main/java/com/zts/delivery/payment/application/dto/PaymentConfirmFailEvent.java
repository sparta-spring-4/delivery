package com.zts.delivery.payment.application.dto;

import com.zts.delivery.order.domain.OrderId;
import lombok.Builder;

@Builder
public record PaymentConfirmFailEvent(
        OrderId orderId
) {
}
