package com.zts.delivery.review.presentation.dto;

import com.zts.delivery.review.application.service.dto.ReviewInfo;
import com.zts.delivery.review.application.service.dto.StoreReviewInfo;
import com.zts.delivery.store.domain.StoreId;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ReviewListResponse(
        StoreId storeId,
        long reviewCount,
        BigDecimal averageScore,
        List<ReviewResponse> reviews
) {
    public static ReviewListResponse of(StoreReviewInfo storeReviewInfo, List<ReviewInfo> reviewInfos) {
        List<ReviewResponse> reviewResponses = reviewInfos.stream()
                .map(ReviewResponse::of)
                .toList();

        return ReviewListResponse.builder()
                .storeId(storeReviewInfo.storeId())
                .reviewCount(storeReviewInfo.reviewCount())
                .averageScore(storeReviewInfo.averageScore())
                .reviews(reviewResponses)
                .build();
    }
}
