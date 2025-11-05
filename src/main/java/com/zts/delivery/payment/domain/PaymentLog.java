package com.zts.delivery.payment.domain;

import org.springframework.http.HttpStatus;

public class PaymentLog {

    private PaymentId paymentId;

    private String errorRequest;

    private String errorResponse;

    private HttpStatus httpStatus;

    private String errorCode;

    private String errorMessage;
}
