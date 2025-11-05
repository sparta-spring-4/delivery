package com.zts.delivery.payment.domain;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.global.persistence.common.DateAudit;
import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.user.domain.UserId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "P_PAYMENTS")
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Payment extends DateAudit {

    @EmbeddedId
    private PaymentId id;

    @Embedded
    private OrderId orderId;

    @Embedded
    private UserId userId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "total_price"))
    })
    private Price totalPrice;

    private String pgTransactionKey;

    @Column(length = 45)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(length = 45)
    @Enumerated(EnumType.STRING)
    private PaymentType type;

    private LocalDateTime requestedAt;

    private LocalDateTime approvedAt;

    @Builder
    public Payment(OrderId orderId, UserId userId, Price totalPrice, String pgTransactionKey, PaymentStatus status, PaymentType type, LocalDateTime requestedAt, LocalDateTime approvedAt) {
        this.id = PaymentId.of();
        this.orderId = orderId;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.pgTransactionKey = pgTransactionKey;
        this.status = status;
        this.type = type;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
    }
}
