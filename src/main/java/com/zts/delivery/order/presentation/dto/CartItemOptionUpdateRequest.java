package com.zts.delivery.order.presentation.dto;

import com.zts.delivery.order.domain.cart.CartId;
import java.util.List;
import lombok.Builder;

@Builder
public record CartItemOptionUpdateRequest(
    CartId cartId,
    int cartItemIndex,
    List<Integer> newOptionIndices
) {

}
