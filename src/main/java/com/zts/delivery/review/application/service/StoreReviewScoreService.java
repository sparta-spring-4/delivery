package com.zts.delivery.review.application.service;

import com.zts.delivery.review.domain.StoreReviewScore;
import com.zts.delivery.review.domain.repository.StoreReviewScoreRepository;
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
public class StoreReviewScoreService {
    private final StoreReviewScoreRepository scoreRepository;

    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class},
            maxAttempts = 6,
            backoff = @Backoff(delay = 500)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateScoreWhenReviewSaved(StoreId storeId, int newReviewScore) {
        StoreReviewScore scoreEntity = scoreRepository.findById(storeId)
                .orElseGet(() -> new StoreReviewScore(storeId));

        scoreEntity.updateScore(newReviewScore);

        scoreRepository.save(scoreEntity);
    }

    @Recover
    public void recoverUpdateScore(ObjectOptimisticLockingFailureException e, StoreId storeId, int newReviewScore) {
        throw new RuntimeException("리뷰 점수 집계 업데이트 실패: 낙관적 잠금 충돌 지속", e);
    }
}
