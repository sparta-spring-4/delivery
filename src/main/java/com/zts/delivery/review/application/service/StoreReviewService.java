package com.zts.delivery.review.application.service;

import com.zts.delivery.global.infrastructure.event.Events;
import com.zts.delivery.global.infrastructure.execption.ApplicationException;
import com.zts.delivery.global.infrastructure.execption.ErrorCode;
import com.zts.delivery.review.application.service.dto.StoreReviewInfo;
import com.zts.delivery.review.application.service.dto.StoreReviewUpdateEvent;
import com.zts.delivery.review.domain.StoreReview;
import com.zts.delivery.review.domain.repository.StoreReviewRepository;
import com.zts.delivery.store.domain.StoreId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreReviewService {
    private final StoreReviewRepository storeReviewRepository;

    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class},
            maxAttempts = 6,
            backoff = @Backoff(delay = 500)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateScoreWhenReviewSaved(StoreId storeId, int newReviewScore) {
        StoreReview scoreEntity = storeReviewRepository.findById(storeId)
                .orElseGet(() -> new StoreReview(storeId));

        scoreEntity.updateScore(newReviewScore);

        StoreReview savedStoreReview = storeReviewRepository.save(scoreEntity);

        Events.trigger(new StoreReviewUpdateEvent(savedStoreReview.getStoreId(), savedStoreReview.getReviewCount(),
                savedStoreReview.getAverageScore()));
    }

    public StoreReviewInfo findBy(StoreId storeId) {
        StoreReview storeReview = storeReviewRepository.findById(storeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, "Not found storeReview"));
        return StoreReviewInfo.of(storeReview);
    }

    @Recover
    public void recoverUpdateScore(ObjectOptimisticLockingFailureException e, StoreId storeId, int newReviewScore) {
        throw new RuntimeException("리뷰 점수 집계 업데이트 실패: 낙관적 잠금 충돌 지속 (storeId=%s, newReviewScore=%d)"
                .formatted(storeId.getId(), newReviewScore), e);
    }
}
