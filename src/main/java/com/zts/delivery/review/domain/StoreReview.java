package com.zts.delivery.review.domain;

import com.zts.delivery.global.persistence.common.DateAudit;
import com.zts.delivery.store.domain.StoreId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Getter
@Table(name = "P_STORE_REVIEW")
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class StoreReview extends DateAudit {

    @EmbeddedId
    private StoreId storeId;

    private int reviewCount;

    private long totalReviewScore;

    private BigDecimal averageScore;

    @Version
    private Long version;

    public StoreReview(StoreId storeId) {
        this.storeId = storeId;
        this.reviewCount = 0;
        this.totalReviewScore = 0;
        this.averageScore = new BigDecimal(0).setScale(2, RoundingMode.HALF_UP);
    }

    public void updateScore(int newReviewScore) {
        long currentTotal = this.totalReviewScore + newReviewScore;
        int currentCount = this.reviewCount + 1;

        BigDecimal newTotal = new BigDecimal(currentTotal);
        BigDecimal newCount = new BigDecimal(currentCount);

        this.averageScore = newTotal.divide(newCount, 2, RoundingMode.HALF_UP);
        this.totalReviewScore = currentTotal;
        this.reviewCount = currentCount;
    }
}
