package com.zts.delivery.review.application.service;

import com.zts.delivery.review.domain.Review;
import com.zts.delivery.review.domain.repository.ReviewRepository;
import com.zts.delivery.store.domain.StoreId;
import com.zts.delivery.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StoreReviewScoreService storeReviewScoreService;


    @Transactional
    public void register(UserId userId, RegisterReview registerReview) {
        Review review = Review.builder()
                .userId(userId)
                .storeId(registerReview.storeId())
                .orderId(registerReview.orderId())
                .comment(registerReview.comment())
                .score(registerReview.score())
                .build();
        reviewRepository.save(review);
        storeReviewScoreService.updateScoreWhenReviewSaved(review.getStoreId(), review.getScore());
    }

    public void findAllBy(StoreId storeId, Pageable pageable) {
        reviewRepository.findAllByStoreId(storeId, pageable);
    }
}
