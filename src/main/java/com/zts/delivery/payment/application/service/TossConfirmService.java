package com.zts.delivery.payment.application.service;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.global.infrastructure.event.Events;
import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.payment.application.dto.ConfirmTossPayment;
import com.zts.delivery.payment.application.dto.PaymentConfirmDoneEvent;
import com.zts.delivery.payment.application.dto.PaymentFailLogEvent;
import com.zts.delivery.payment.domain.Payment;
import com.zts.delivery.payment.domain.PaymentMethod;
import com.zts.delivery.payment.domain.PaymentStatus;
import com.zts.delivery.payment.domain.PaymentType;
import com.zts.delivery.payment.domain.exception.PaymentPriceWrongException;
import com.zts.delivery.payment.domain.repository.PaymentRepository;
import com.zts.delivery.payment.domain.service.PayOrderPriceValidator;
import com.zts.delivery.payment.infrastructure.client.TossClientErrorException;
import com.zts.delivery.payment.infrastructure.client.TossPaymentClientResponse;
import com.zts.delivery.payment.infrastructure.client.confirm.TossPaymentConfirmClient;
import com.zts.delivery.payment.infrastructure.client.confirm.TossPaymentConfirmClientRequest;
import com.zts.delivery.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TossConfirmService {

    private final TossPaymentConfirmClient confirmClient;
    private final PaymentRepository paymentRepository;
    private final PayOrderPriceValidator orderPriceValidator;

    public void confirm(UserId userId, ConfirmTossPayment confirmTossPayment) {
        log.info("결제 승인 시작 (orderId: {})", confirmTossPayment.orderId());
        priceOrderValidate(confirmTossPayment.orderId(), confirmTossPayment.amount());

        TossPaymentClientResponse response = null;
        try {
            response = requestConfirmation(confirmTossPayment);
        } catch (TossClientErrorException e) {
            PaymentFailLogEvent event = createConfirmFailLogEvent(userId, confirmTossPayment, e);
            Events.trigger(event);
            throw e;
        }

        Payment payment = createConfirmedPayment(userId, response);
        paymentRepository.saveAndFlush(payment);
        log.info("결제 승인 완료 (orderId: {})", confirmTossPayment.orderId());

        Events.trigger(new PaymentConfirmDoneEvent(payment.getOrderId()));
    }

    private TossPaymentClientResponse requestConfirmation(ConfirmTossPayment confirmTossPayment) {
        TossPaymentConfirmClientRequest request = TossPaymentConfirmClientRequest.builder()
                .orderId(confirmTossPayment.orderId().getId().toString())
                .amount(confirmTossPayment.amount().getValue())
                .paymentKey(confirmTossPayment.paymentKey())
                .build();
        return confirmClient.confirm(request);
    }

    private Payment createConfirmedPayment(UserId userId, TossPaymentClientResponse response) {
        return Payment.builder()
                .paymentKey(response.paymentKey())
                .orderId(OrderId.of(UUID.fromString(response.orderId())))
                .totalPrice(new Price(response.totalAmount()))
                .requestedAt(response.requestedAt().toLocalDateTime())
                .approvedAt(response.approvedAt().toLocalDateTime())
                .userId(userId)
                .status(PaymentStatus.DONE)
                .type(PaymentType.TOSS)
                .build();
    }

    private PaymentFailLogEvent createConfirmFailLogEvent(UserId userId, ConfirmTossPayment confirmTossPayment, TossClientErrorException e) {
        return PaymentFailLogEvent.builder()
                .orderId(confirmTossPayment.orderId())
                .userId(userId)
                .paymentKey(confirmTossPayment.paymentKey())
                .paymentType(PaymentType.TOSS)
                .paymentMethod(PaymentMethod.CONFIRM)
                .totalPrice(confirmTossPayment.amount())
                .httpStatus((HttpStatus) e.getStatusCode())
                .errorCode(e.getCode())
                .errorMessage(e.getMessage())
                .erroredAt(LocalDateTime.now())
                .build();
    }

    private void priceOrderValidate(OrderId orderId, Price amount) {
        boolean validated = orderPriceValidator.validate(orderId, amount);
        if (validated) {
            throw new PaymentPriceWrongException();
        }
    }
}
