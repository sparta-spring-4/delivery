package com.zts.delivery.review.application.service;

import com.zts.delivery.review.application.service.dto.RegisterReview;
import com.zts.delivery.review.application.service.dto.ReviewInfo;
import com.zts.delivery.review.domain.Review;
import com.zts.delivery.review.domain.repository.ReviewRepository;
import com.zts.delivery.store.domain.StoreId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StoreReviewService storeReviewScoreService;

    @Transactional
    public void register(RegisterReview registerReview) {
        Review review = Review.builder()
                .userId(registerReview.userId())
                .username(registerReview.username())
                .storeId(registerReview.storeId())
                .orderId(registerReview.orderId())
                .comment(registerReview.comment())
                .score(registerReview.score())
                .build();
        reviewRepository.save(review);
        storeReviewScoreService.updateScoreWhenReviewSaved(review.getStoreId(), review.getScore());
    }

    public List<ReviewInfo> findAllBy(StoreId storeId, Pageable pageable) {
        List<Review> reviews = reviewRepository.findAllByStoreId(storeId, pageable);
        return reviews.stream()
                .map(ReviewInfo::of)
                .toList();
    }
}
