package com.zts.delivery.payment.domain;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.global.persistence.common.DateAudit;
import com.zts.delivery.order.domain.OrderId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@Entity
@Table(name = "P_PAYMENT_LOGS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentLog extends DateAudit {

    @EmbeddedId
    private PaymentLogId id;


    @Embedded
    private OrderId orderId;

    @Enumerated(EnumType.STRING)
    @Column(length = 45)
    private PaymentType paymentType;

    private String paymentKey;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "total_price"))
    })
    private Price totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(length = 45)
    private PaymentErrorType errorType;

    @Enumerated(EnumType.STRING)
    @Column(length = 45)
    private HttpStatus httpStatus;

    @Column(length = 100)
    private String errorCode;

    @Column(length = 255)
    private String errorMessage;


    @Builder
    public PaymentLog(OrderId orderId, PaymentType paymentType, String paymentKey, Price totalPrice, PaymentErrorType errorType, HttpStatus httpStatus, String errorCode, String errorMessage) {
        this.id = PaymentLogId.of();
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.paymentType = paymentType;
        this.totalPrice = totalPrice;
        this.errorType = errorType;
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
