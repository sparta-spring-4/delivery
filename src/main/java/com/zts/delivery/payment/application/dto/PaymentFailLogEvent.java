package com.zts.delivery.payment.application.dto;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.payment.domain.PaymentMethod;
import com.zts.delivery.payment.domain.PaymentType;
import com.zts.delivery.user.domain.UserId;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Builder
public record PaymentFailLogEvent(
        OrderId orderId,
        UserId userId,
        PaymentType paymentType,
        String paymentKey,
        Price totalPrice,
        PaymentMethod paymentMethod,
        HttpStatus httpStatus,
        String errorCode,
        String errorMessage,
        LocalDateTime erroredAt,
        String cancelReason
) {
}
