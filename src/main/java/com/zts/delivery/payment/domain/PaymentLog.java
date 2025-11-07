package com.zts.delivery.payment.domain;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.global.persistence.common.DateAudit;
import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.payment.domain.converter.ConfirmErrorResponseConverter;
import com.zts.delivery.user.domain.UserId;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@ToString
@Getter
@Entity
@Table(name = "P_PAYMENT_LOGS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentLog extends DateAudit {

    @EmbeddedId
    private PaymentLogId id;

    @Embedded
    private UserId userId;

    @Embedded
    private OrderId orderId;

    @Enumerated(EnumType.STRING)
    @Column(length = 45)
    private PaymentType paymentType;

    private String paymentKey;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "total_price"))
    private Price totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(length = 45)
    private PaymentMethod paymentMethod;

    private String cancelReason;

    @Lob
    @Convert(converter = ConfirmErrorResponseConverter.class)
    private List<PaymentErrorResponse> errorResponses;

    private boolean isSuccess;

    // 현재 시도가 몇 번째 시도인지
    private int retryCount;


    @Builder
    public PaymentLog(OrderId orderId, UserId userId, PaymentType paymentType, String paymentKey, Price totalPrice,
                      PaymentMethod paymentMethod, String cancelReason, List<PaymentErrorResponse> errorResponses) {
        this.id = PaymentLogId.of();
        this.orderId = orderId;
        this.userId = userId;
        this.paymentType = paymentType;
        this.paymentKey = paymentKey;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.errorResponses = errorResponses;
        this.isSuccess = false;
        this.cancelReason = cancelReason;
    }

    public void addLog(PaymentErrorResponse errorResponse) {
        errorResponses.add(errorResponse);
        retryCount += 1;
    }

    public boolean isMaxRetried(int maxRetryCount) {
        return this.retryCount >= maxRetryCount;
    }

    public void success() {
        isSuccess = true;
    }
}
