package com.zts.delivery.payment.application.dto;

import com.zts.delivery.order.domain.OrderId;

public record PaymentCancelDoneEvent(
        OrderId orderId
) {
}
