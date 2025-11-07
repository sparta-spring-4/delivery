package com.zts.delivery.payment.application.dto;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.order.domain.OrderId;
import lombok.Builder;

@Builder
public record OrderCancelEvent(
        OrderId orderId,
        Price cancelAmount,
        String cancelReason
) {
}
