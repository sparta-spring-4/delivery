package com.zts.delivery.review.application.service.dto;

import com.zts.delivery.review.domain.StoreReview;
import com.zts.delivery.store.domain.StoreId;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record StoreReviewInfo(
        StoreId storeId,
        int reviewCount,
        BigDecimal averageScore
) {
    public static StoreReviewInfo of(StoreReview storeReview) {
        return StoreReviewInfo.builder()
                .storeId(storeReview.getStoreId())
                .reviewCount(storeReview.getReviewCount())
                .averageScore(storeReview.getAverageScore())
                .build();
    }
}
