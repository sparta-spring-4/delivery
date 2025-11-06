package com.zts.delivery.order.presentation.dto;

import com.zts.delivery.order.domain.Order;
import java.util.UUID;

public record OrderResponse(
    UUID orderId,
    long orderVersion
) {
    public static OrderResponse of(Order order) {
        return new OrderResponse(
            order.getId().getId(),
            order.getVersion()
        );
    }
}
