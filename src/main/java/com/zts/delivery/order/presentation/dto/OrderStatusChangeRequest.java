package com.zts.delivery.order.presentation.dto;

import com.zts.delivery.order.domain.OrderId;
import lombok.Builder;

@Builder
public record OrderStatusChangeRequest(
    OrderId id
) {

}
