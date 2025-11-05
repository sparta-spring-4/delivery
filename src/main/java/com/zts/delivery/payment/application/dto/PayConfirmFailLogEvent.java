package com.zts.delivery.payment.application.dto;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.order.domain.OrderId;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record PayConfirmFailLogEvent (
        OrderId orderId,
        String paymentKey,
        Price totalPrice,
        HttpStatus httpStatus,
        String errorCode,
        String errorMessage
){
}
