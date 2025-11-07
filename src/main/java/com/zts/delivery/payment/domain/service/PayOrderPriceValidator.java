package com.zts.delivery.payment.domain.service;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.infrastructure.execption.ApplicationException;
import com.zts.delivery.infrastructure.execption.ErrorCode;
import com.zts.delivery.order.domain.Order;
import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.order.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PayOrderPriceValidator {

    private final OrderRepository orderRepository;

    public boolean validate(OrderId orderId, Price price) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, "Not found Order"));
        return order.getTotalOrderPrice().equals(price);
    }
}
