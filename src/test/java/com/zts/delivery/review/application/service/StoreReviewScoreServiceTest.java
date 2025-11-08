package com.zts.delivery.review.application.service;

import com.zts.delivery.review.domain.StoreReview;
import com.zts.delivery.review.domain.repository.StoreReviewRepository;
import com.zts.delivery.store.domain.StoreId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest
@ActiveProfiles("test")
class StoreReviewScoreServiceTest {

    @Autowired
    private StoreReviewService scoreService;

    @Autowired
    private StoreReviewRepository scoreRepository;

    private static final StoreId TEST_STORE_ID = new StoreId(UUID.randomUUID());
    private static final int THREAD_COUNT = 5;
    private static final int REVIEW_SCORE_PER_THREAD = 5; // 각 스레드가 부여할 리뷰 점수

    @BeforeEach
    void setup() {
        // 기존 엔티티가 남아있을 경우를 대비하여 삭제
        scoreRepository.deleteById(TEST_STORE_ID);
        scoreRepository.flush();

        // 초기 엔티티 생성 및 저장 (version = 0)
        StoreReview initialScore = new StoreReview(TEST_STORE_ID);
        scoreRepository.saveAndFlush(initialScore);
    }

    @AfterEach
    void tearDown() {
        scoreRepository.deleteById(TEST_STORE_ID);
    }

    @Test
    @DisplayName("멀티스레드 환경에서 낙관적 잠금 충돌 발생 및 Retryable을 통한 성공적 업데이트 검증")
    void shouldHandleOptimisticLockingFailureAndRetrySuccessfully() throws InterruptedException {
        // Given
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        // When
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    scoreService.updateScoreWhenReviewSaved(TEST_STORE_ID, REVIEW_SCORE_PER_THREAD);
                } catch (Exception e) {
                    fail("Retryable 로직이 최대 재시도 횟수를 초과하여 실패했습니다: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드가 완료될 때까지 대기
        latch.await();
        executorService.shutdown();

        // Then
        StoreReview finalScore = scoreRepository.findById(TEST_STORE_ID)
                .orElseThrow(() -> new AssertionError("StoreReviewScore 엔티티를 찾을 수 없음"));

        // 1. 최종 리뷰 카운트 검증
        long expectedReviewCount = THREAD_COUNT;
        assertThat(finalScore.getReviewCount())
                .as("최종 리뷰 카운트는 모든 스레드 수와 같아야 함")
                .isEqualTo(expectedReviewCount);

        // 2. 최종 총 리뷰 점수 검증
        long expectedTotalReviewScore = THREAD_COUNT * REVIEW_SCORE_PER_THREAD;
        assertThat(finalScore.getTotalReviewScore())
                .as("최종 총 리뷰 점수는 (스레드 수 * 점수)와 같아야 함")
                .isEqualTo(expectedTotalReviewScore);

        // 3. 최종 평균 점수 검증
        BigDecimal expectedAverageScore = new BigDecimal(expectedTotalReviewScore)
                .divide(new BigDecimal(expectedReviewCount), 2, RoundingMode.HALF_UP);
        assertThat(finalScore.getAverageScore())
                .as("최종 평균 점수는 정확하게 계산되어야 함")
                .isEqualByComparingTo(expectedAverageScore);

        // 4. 버전 검증: 충돌이 발생했으므로 버전은 THREAD_COUNT보다 크거나 같아야 합니다.
        // T * N (성공 커밋) + T_retry (실패 및 재시도로 인한 추가 시도)
        assertThat(finalScore.getVersion())
                .as("버전은 최소한 성공적인 커밋 수(THREAD_COUNT) 이상 증가해야 함")
                .isGreaterThanOrEqualTo((long) THREAD_COUNT);
    }
}