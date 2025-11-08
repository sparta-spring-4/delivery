package com.zts.delivery.order.application;

import com.zts.delivery.order.domain.Order;
import com.zts.delivery.order.domain.OrderRepository;
import com.zts.delivery.payment.application.dto.PaymentCancelDoneEvent;
import com.zts.delivery.payment.application.dto.PaymentConfirmDoneEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderRepository orderRepository;

    @Async
    @EventListener
    @Transactional
    public void handle(PaymentConfirmDoneEvent event) {
        log.info("결제 승인 이벤트 수신(oderId: {})", event.orderId());
        orderRepository.findById(event.orderId())
                .ifPresent(Order::paymentComplete);
    }

    @Async
    @EventListener
    @Transactional
    public void handle(PaymentCancelDoneEvent event) {
        log.info("결제 취소 이벤트 수신(oderId: {})", event.orderId());
        orderRepository.findById(event.orderId())
                .ifPresent(Order::refundComplete);
    }

}
