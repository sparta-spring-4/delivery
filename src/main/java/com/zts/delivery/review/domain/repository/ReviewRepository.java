package com.zts.delivery.review.domain.repository;

import com.zts.delivery.review.domain.Review;
import com.zts.delivery.review.domain.ReviewId;
import com.zts.delivery.store.domain.StoreId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, ReviewId> {

    List<Review> findAllByStoreId(StoreId storeId, Pageable pageable);
}
