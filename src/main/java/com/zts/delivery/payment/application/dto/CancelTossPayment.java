package com.zts.delivery.payment.application.dto;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.order.domain.OrderId;
import lombok.Builder;

@Builder
public record CancelTossPayment(
        OrderId orderId,
        Price cancelAmount,
        String refundReason
) {
}
