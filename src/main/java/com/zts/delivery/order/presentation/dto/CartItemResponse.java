package com.zts.delivery.order.presentation.dto;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.order.domain.cart.CartItem;
import java.util.List;
import lombok.Builder;

@Builder
public record CartItemResponse(

    String itemName,
    int quantity,
    Price linePrice,
    List<Integer> selectedOptionIndices
) {
    /**
     * CartItem 값 객체를 CartItemResponse DTO로 변환
     */
    public static CartItemResponse from(CartItem cartItem) {
        return CartItemResponse.builder()
            .quantity(cartItem.getQuantity())
            .linePrice(cartItem.getPrice())
            .selectedOptionIndices(cartItem.getSelectedOptions())
            .build();
    }
}