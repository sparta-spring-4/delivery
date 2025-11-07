package com.zts.delivery.review.application.service;

import com.zts.delivery.review.domain.Review;
import com.zts.delivery.review.domain.ReviewRepository;
import com.zts.delivery.store.StoreId;
import com.zts.delivery.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional
    void register(UserId userId, RegisterReview registerReview) {
        Review review = Review.builder()
                .userId(userId)
                .storeId(registerReview.storeId())
                .orderId(registerReview.orderId())
                .comment(registerReview.comment())
                .score(registerReview.score())
                .build();
        reviewRepository.save(review);
    }

    void findAllBy(StoreId storeId) {

    }
}
