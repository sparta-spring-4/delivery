package com.zts.delivery.payment.application.service;

import com.zts.delivery.payment.application.dto.PayConfirmFailLogEvent;
import com.zts.delivery.payment.domain.PaymentErrorType;
import com.zts.delivery.payment.domain.PaymentLog;
import com.zts.delivery.payment.domain.PaymentLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentLogEventListener {

    private final PaymentLogRepository paymentLogRepository;

    /**
     * 결제 실패 이벤트를 수신하여 PaymentLog를 DB에 저장합니다.
     *
     * @param event 결제 실패 이벤트
     */
    @Async
    @EventListener
    public void handlePaymentFailedEvent(PayConfirmFailLogEvent event) {
        log.info("결제 실패 이벤트 수신, 로그 저장 시작 (orderId: {})", event.orderId());

        PaymentLog paymentLog = PaymentLog.builder()
                .orderId(event.orderId())
                .paymentKey(event.paymentKey())
                .totalPrice(event.totalPrice())
                .errorType(PaymentErrorType.CONFIRM)
                .httpStatus(event.httpStatus())
                .errorCode(event.errorCode())
                .errorMessage(event.errorMessage())
                .build();

        paymentLogRepository.save(paymentLog);
        log.info("PaymentLog 저장 완료 (orderId: {})", event.orderId());
    }
}
