package com.zts.delivery.order.application;

import com.zts.delivery.order.application.dto.OrderCancelEvent;
import com.zts.delivery.order.domain.Order;
import com.zts.delivery.order.domain.OrderRepository;
import com.zts.delivery.payment.application.dto.PaymentCancelDoneEvent;
import com.zts.delivery.payment.application.dto.PaymentConfirmDoneEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderRepository orderRepository;

    @Async
    @EventListener
    public void handle(PaymentConfirmDoneEvent event) {
        orderRepository.findById(event.orderId())
                .ifPresent(Order::paymentComplete);
    }

    @Async
    @EventListener
    public void handle(PaymentCancelDoneEvent event) {
        orderRepository.findById(event.orderId())
            .ifPresent(Order::refundComplete);
    }

}
