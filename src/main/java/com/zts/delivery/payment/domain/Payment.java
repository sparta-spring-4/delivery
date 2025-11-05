package com.zts.delivery.payment.domain;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.global.persistence.common.DateAudit;
import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.user.domain.UserId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "P_PAYMENTS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends DateAudit {

    @EmbeddedId
    private PaymentId id;

    private OrderId orderId;

    private UserId userId;

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

}
