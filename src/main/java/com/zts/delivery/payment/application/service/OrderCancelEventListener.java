package com.zts.delivery.payment.application.service;


import com.zts.delivery.payment.application.dto.CancelTossPayment;
import com.zts.delivery.payment.application.dto.OrderCancelEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCancelEventListener {

    private final TossCancelService cancelService;

    @Async
    @EventListener
    public void handle(OrderCancelEvent event) {
        CancelTossPayment cancelTossPayment = CancelTossPayment.builder()
                .orderId(event.orderId())
                .cancelAmount(event.cancelAmount())
                .cancelReason(event.cancelReason())
                .build();
        cancelService.cancel(cancelTossPayment);
    }
}
