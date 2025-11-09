package com.zts.delivery.review.presentation.dto;

import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.review.application.service.dto.ReviewInfo;
import com.zts.delivery.review.domain.ReviewId;
import com.zts.delivery.store.domain.StoreId;
import com.zts.delivery.user.domain.UserId;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReviewResponse(
        ReviewId id,
        StoreId storeId,
        OrderId orderId,
        UserId userId,
        String username,
        String comment,
        int score,
        LocalDateTime updatedAt
) {
    public static ReviewResponse of(ReviewInfo review) {
        return ReviewResponse.builder()
                .id(review.id())
                .storeId(review.storeId())
                .userId(review.userId())
                .username(review.username())
                .score(review.score())
                .updatedAt(review.updatedAt())
                .build();
    }
}
