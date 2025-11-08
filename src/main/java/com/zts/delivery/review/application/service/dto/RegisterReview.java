package com.zts.delivery.review.application.service.dto;

import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.store.domain.StoreId;
import com.zts.delivery.user.domain.UserId;
import lombok.Builder;

@Builder
public record RegisterReview(
        UserId userId,
        String username,
        OrderId orderId,
        StoreId storeId,
        String comment,
        int score
) {
}
