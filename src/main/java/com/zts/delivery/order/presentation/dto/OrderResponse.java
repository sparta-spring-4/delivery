package com.zts.delivery.order.presentation.dto;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.order.domain.*;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record OrderResponse(
        UUID orderId,
        Orderer orderer,
        List<OrderItemResponse> orderItems,
        Price totalOrderPrice,
        OrderStatus status,
        DeliveryInfo deliveryInfo,
        int totalPrice

) {
    public static OrderResponse of(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId().getId())
                .orderer(order.getOrderer())
                .orderItems(
                        order.getOrderItems().stream().map(OrderItemResponse::of).toList()
                )
                .status(order.getStatus())
                .deliveryInfo(order.getDeliveryInfo())
                .totalPrice(order.getTotalOrderPrice().getValue())
                .build();
    }

    @Builder
    public record OrderItemResponse(
            String itemName,
            int quantity,
            List<OrderItemOption> options,
            int price
    ) {
        public static OrderItemResponse of(OrderItem orderItem) {
            return OrderItemResponse.builder()
                    .itemName(orderItem.getItemName())
                    .quantity(orderItem.getQuantity())
                    .options(orderItem.getOptions())
                    .price(orderItem.getPrice().getValue())
                    .build();
        }
    }
}
