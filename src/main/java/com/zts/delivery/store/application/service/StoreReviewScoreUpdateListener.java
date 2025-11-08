package com.zts.delivery.store.application.service;

import com.zts.delivery.review.application.service.dto.StoreReviewUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoreReviewScoreUpdateListener {

    private final StoreUpdateService storeUpdateService;

    @Async
    @EventListener
    public void handle(StoreReviewUpdateEvent event) {
        storeUpdateService.updateReview(event.storeId().getId(), event.reviewCount(), event.averageStoreReview());
    }
}
