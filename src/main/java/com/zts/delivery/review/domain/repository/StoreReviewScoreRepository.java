package com.zts.delivery.review.domain.repository;

import com.zts.delivery.review.domain.StoreReviewScore;
import com.zts.delivery.store.domain.StoreId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreReviewScoreRepository extends JpaRepository<StoreReviewScore, StoreId> {

}
