package com.zts.delivery.payment.domain.exception;

import com.zts.delivery.global.infrastructure.execption.ApplicationException;
import com.zts.delivery.global.infrastructure.execption.ErrorCode;

public class PaymentPriceWrongException extends ApplicationException {

    public PaymentPriceWrongException() {
        super(ErrorCode.BAD_REQUEST, "주문금액과 결제금액이 일치하지 않습니다.");
    }
}
