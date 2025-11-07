package com.zts.delivery.review.application.service;

import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.store.StoreId;
import lombok.Builder;

@Builder
public record RegisterReview(
        OrderId orderId,
        StoreId storeId,
        String comment,
        int score
) {
}
