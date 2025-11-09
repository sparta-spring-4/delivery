package com.zts.delivery.review.domain.repository;

import com.zts.delivery.review.domain.StoreReview;
import com.zts.delivery.store.domain.StoreId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreReviewRepository extends JpaRepository<StoreReview, StoreId> {

}
