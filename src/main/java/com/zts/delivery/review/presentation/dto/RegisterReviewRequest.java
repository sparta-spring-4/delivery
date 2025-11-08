package com.zts.delivery.review.presentation.dto;

import java.util.UUID;

public record RegisterReviewRequest(
        UUID orderId,
        UUID storeId,
        String comment,
        int score
) {
}
