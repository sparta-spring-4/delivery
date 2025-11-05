package com.zts.delivery.order.presentation.dto;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.order.domain.cart.Cart;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CartResponse(
    UUID cartId,
    UUID userId,
    List<UUID> cartItemIds,
    Price price
) {

    public static CartResponse of(Cart cart) {
        List<UUID> cartItemIds = cart.getCartItems().stream()
            .map(cartItem -> cartItem.getId().getId())
            .toList();

        return CartResponse.builder()
            .cartId(cart.getId().getId())
            .userId(cart.getUserId().getId())
            .cartItemIds(cartItemIds)
            .price(cart.getPrice())
            .build();
    }
}
