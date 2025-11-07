package com.zts.delivery.order.presentation.dto;

import com.zts.delivery.order.domain.OrderId;

public record OrderReadRequest(
    OrderId id
) {

}
