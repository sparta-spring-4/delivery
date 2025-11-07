package com.zts.delivery.payment.application.service;

import com.zts.delivery.payment.application.dto.PaymentFailLogEvent;
import com.zts.delivery.payment.domain.PaymentErrorResponse;
import com.zts.delivery.payment.domain.PaymentLog;
import com.zts.delivery.payment.domain.repository.PaymentLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public void handle(PaymentFailLogEvent event) {
        log.warn("결제 실패 이벤트 수신, 로그 저장 시작 (method: {} orderId: {})", event.paymentMethod(), event.orderId());
        PaymentErrorResponse errorResponse = createErrorResponse(event);

        PaymentLog paymentLog = paymentLogRepository
                .findByOrderIdAndPaymentMethod(event.orderId(), event.paymentMethod())
                .map(log -> {
                    log.addLog(errorResponse);
                    return log;
                })
                .orElseGet(() -> createPaymentLog(event, errorResponse));

        paymentLogRepository.save(paymentLog);
        log.info("PaymentLog 저장 완료 (method: {}, orderId: {})", event.paymentMethod(), event.orderId());
    }

    private PaymentErrorResponse createErrorResponse(PaymentFailLogEvent event) {
        return PaymentErrorResponse.builder()
                .httpStatus(event.httpStatus())
                .errorCode(event.errorCode())
                .errorMessage(event.errorMessage())
                .erroredAt(event.erroredAt())
                .build();
    }

    private PaymentLog createPaymentLog(PaymentFailLogEvent event, PaymentErrorResponse errorResponse) {
        return PaymentLog.builder()
                .orderId(event.orderId())
                .userId(event.userId())
                .paymentKey(event.paymentKey())
                .totalPrice(event.totalPrice())
                .paymentType(event.paymentType())
                .paymentMethod(event.paymentMethod())
                .errorResponses(List.of(errorResponse))
                .build();
    }
}
