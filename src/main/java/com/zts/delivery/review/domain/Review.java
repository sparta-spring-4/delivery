package com.zts.delivery.review.domain;

import com.zts.delivery.global.persistence.common.DateAudit;
import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.store.domain.StoreId;
import com.zts.delivery.user.domain.UserId;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "P_REVIEW")
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Review extends DateAudit {

    @EmbeddedId
    private ReviewId id;

    @Embedded
    private StoreId storeId;

    @Embedded
    private OrderId orderId;

    @Embedded
    private UserId userId;

    private String comment;

    private int score;

    @Builder
    public Review(StoreId storeId, OrderId orderId, UserId userId, String comment, int score) {
        this.id = ReviewId.of();
        this.storeId = storeId;
        this.orderId = orderId;
        this.userId = userId;
        this.comment = comment;
        this.score = score;
    }
}
