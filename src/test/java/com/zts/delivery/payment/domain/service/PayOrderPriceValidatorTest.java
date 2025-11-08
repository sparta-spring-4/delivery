package com.zts.delivery.payment.domain.service;


import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.order.domain.Order;
import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.order.domain.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class PayOrderPriceValidatorTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PayOrderPriceValidator validator;

    OrderId orderId;

    Price orderPrice;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAllInBatch();

        orderPrice = new Price(50000);
        Order order = Order.create(null, null, null, orderPrice);
        orderRepository.save(order);
        orderId = order.getId();
    }

    @Test
    @DisplayName("주문 금액과 결제 금액이 같은지 비교한다.")
    void validateOrder() {
        boolean validated = validator.validate(orderId, new Price(50000));
        assertThat(validated).isTrue();
    }
}