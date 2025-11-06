package com.zts.delivery.payment.application.service;

import com.zts.delivery.payment.application.dto.PaymentFailLogEvent;
import com.zts.delivery.payment.domain.ConfirmErrorResponse;
import com.zts.delivery.payment.domain.PaymentLog;
import com.zts.delivery.payment.domain.repository.PaymentLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public void handlePaymentFailedEvent(PaymentFailLogEvent event) {
        log.warn("결제 실패 이벤트 수신, 로그 저장 시작 (method: {} orderId: {})", event.paymentMethod(), event.orderId());

        ConfirmErrorResponse errorResponse = ConfirmErrorResponse.builder()
                .httpStatus(event.httpStatus())
                .errorCode(event.errorCode())
                .errorMessage(event.errorMessage())
                .erroredAt(event.erroredAt())
                .build();

        PaymentLog paymentLog = null;
        Optional<PaymentLog> paymentLogOpt = paymentLogRepository.findByOrderId(event.orderId());
        if (paymentLogOpt.isPresent()) {
            paymentLog = paymentLogOpt.get();
            paymentLog.addLog(errorResponse);
        } else {
            paymentLog = PaymentLog.builder()
                    .orderId(event.orderId())
                    .userId(event.userId())
                    .paymentKey(event.paymentKey())
                    .totalPrice(event.totalPrice())
                    .paymentType(event.paymentType())
                    .paymentMethod(event.paymentMethod())
                    .errorResponses(List.of(errorResponse))
                    .build();
        }
        paymentLogRepository.save(paymentLog);
        log.info("PaymentLog 저장 완료 (method: {}, orderId: {})", event.paymentMethod(), event.orderId());
    }
}
