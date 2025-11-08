package com.zts.delivery.review.application.service.dto;


import com.zts.delivery.store.domain.StoreId;

import java.math.BigDecimal;

public record StoreReviewUpdateEvent(
        StoreId storeId,
        int reviewCount,
        BigDecimal averageStoreReview
) {
}
