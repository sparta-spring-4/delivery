package com.zts.delivery.order.presentation.dto;

import com.zts.delivery.order.domain.cart.CartId;
import lombok.Builder;

@Builder
public record CartUpdateRequest(
    CartId cartId,
    int cartItemIndex,
    boolean isAdding
) {

}
