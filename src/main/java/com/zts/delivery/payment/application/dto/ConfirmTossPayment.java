package com.zts.delivery.payment.application.dto;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.order.domain.OrderId;
import lombok.Builder;

@Builder
public record ConfirmTossPayment(
        OrderId orderId,
        String paymentKey,
        Price amount
) {
}
