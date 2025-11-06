package com.zts.delivery.payment.application.service;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.infrastructure.event.Events;
import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.payment.application.dto.ConfirmTossPayment;
import com.zts.delivery.payment.application.dto.PayConfirmFailLogEvent;
import com.zts.delivery.payment.application.dto.PaymentDoneEvent;
import com.zts.delivery.payment.domain.Payment;
import com.zts.delivery.payment.domain.PaymentStatus;
import com.zts.delivery.payment.domain.PaymentType;
import com.zts.delivery.payment.domain.repository.PaymentRepository;
import com.zts.delivery.payment.infrastructure.client.TossClientErrorException;
import com.zts.delivery.payment.infrastructure.client.TossPaymentConfirmClient;
import com.zts.delivery.payment.infrastructure.client.TossPaymentConfirmClientRequest;
import com.zts.delivery.payment.infrastructure.client.TossPaymentConfirmClientResponse;
import com.zts.delivery.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TossConfirmService {

    private final TossPaymentConfirmClient confirmClient;
    private final PaymentRepository paymentRepository;

    public void confirm(ConfirmTossPayment confirmTossPayment) {
        TossPaymentConfirmClientResponse response = null;
        try {
            response = requestConfirmation(confirmTossPayment);
        } catch (TossClientErrorException e) {
            PayConfirmFailLogEvent event = createPayPayConfirmFailLogEvent(confirmTossPayment, e);
            Events.trigger(event);
            throw e;
        }

        Payment payment = createConfirmedPayment(confirmTossPayment, response);
        paymentRepository.save(payment);

        Events.trigger(new PaymentDoneEvent(payment.getOrderId()));
    }

    /**
     * 실패한 결제 재시도
     * - 실패 횟수 기록, 3이상 실패 -> 주문 취소
     */
    @Scheduled(fixedDelay = 1L, timeUnit = TimeUnit.SECONDS)
    public void retryConfirmPayment() {
    }

    private TossPaymentConfirmClientResponse requestConfirmation(ConfirmTossPayment confirmTossPayment) {
        TossPaymentConfirmClientRequest request = TossPaymentConfirmClientRequest.builder()
                .orderId(confirmTossPayment.orderId())
                .amount(confirmTossPayment.amount())
                .paymentKey(confirmTossPayment.paymentKey())
                .build();
        return confirmClient.confirm(request);
    }

    private Payment createConfirmedPayment(ConfirmTossPayment confirmTossPayment, TossPaymentConfirmClientResponse response) {
        return Payment.builder()
                .paymentKey(response.paymentKey())
                .orderId(OrderId.of(UUID.fromString(response.orderId())))
                .totalPrice(new Price(response.totalAmount()))
                .requestedAt(response.requestedAt().toLocalDateTime())
                .approvedAt(response.approvedAt().toLocalDateTime())
                .userId(UserId.of(confirmTossPayment.userId()))
                .status(PaymentStatus.DONE).type(PaymentType.TOSS)
                .build();
    }

    private PayConfirmFailLogEvent createPayPayConfirmFailLogEvent(ConfirmTossPayment confirmTossPayment, TossClientErrorException e) {
        return PayConfirmFailLogEvent.builder()
                .orderId(OrderId.of(UUID.fromString(confirmTossPayment.orderId())))
                .paymentKey(confirmTossPayment.paymentKey())
                .paymentType(PaymentType.TOSS)
                .totalPrice(new Price(confirmTossPayment.amount()))
                .httpStatus((HttpStatus) e.getStatusCode())
                .errorCode(e.getCode())
                .errorMessage(e.getMessage())
                .erroredAt(LocalDateTime.now())
                .build();
    }
}
