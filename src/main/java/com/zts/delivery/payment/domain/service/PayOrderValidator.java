package com.zts.delivery.payment.domain.service;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.order.domain.OrderId;
import org.springframework.stereotype.Component;

@Component
public class PayOrderValidator {
    public boolean validate(OrderId orderId, Price price) {

        return false;
    }
}
