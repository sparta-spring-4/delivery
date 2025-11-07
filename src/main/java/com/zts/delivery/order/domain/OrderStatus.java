package com.zts.delivery.order.domain;

public enum OrderStatus {
    ORDER_CREATED,
    PAYMENT_CONFIRM,
    PREPARING,
    DELIVERING,
    DELIVERED,
    ORDER_DONE,
    ORDER_CANCEL,
    ORDER_REFUND,
    EXCHANGED
}
