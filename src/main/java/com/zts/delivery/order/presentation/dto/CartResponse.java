package com.zts.delivery.order.presentation.dto;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.order.domain.cart.Cart;
import lombok.Builder;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode;

@Builder
public record CartResponse(
    Price totalCartPrice,
    int totalItemLines
    // int totalItemQuantity
    // List<CartItemResponse> items
) {

    public static CartResponse from(Cart cart) {
        return CartResponse.builder()
            .totalCartPrice(cart.getTotalPrice())
            // .items(cart.getCartItems().stream()
            //     .map(CartItemResponse::from)
            //     .toList())
            .build();
    }
}
