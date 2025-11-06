package com.zts.delivery.payment.application.dto;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.payment.domain.PaymentType;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Builder
public record PayConfirmFailLogEvent(
        OrderId orderId,
        PaymentType paymentType,
        String paymentKey,
        Price totalPrice,
        HttpStatus httpStatus,
        String errorCode,
        String errorMessage,
        LocalDateTime erroredAt
) {
}
