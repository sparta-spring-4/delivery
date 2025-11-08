package com.zts.delivery.review.application.service.dto;

import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.review.domain.Review;
import com.zts.delivery.review.domain.ReviewId;
import com.zts.delivery.store.domain.StoreId;
import com.zts.delivery.user.domain.UserId;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReviewInfo(
        ReviewId id,
        StoreId storeId,
        OrderId orderId,
        UserId userId,
        String username,
        String comment,
        int score,
        LocalDateTime updatedAt
) {
    public static ReviewInfo of(Review review) {
        return ReviewInfo.builder()
                .id(review.getId())
                .storeId(review.getStoreId())
                .userId(review.getUserId())
                .username(review.getUsername())
                .score(review.getScore())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
