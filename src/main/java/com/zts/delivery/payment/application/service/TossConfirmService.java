package com.zts.delivery.payment.application.service;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.infrastructure.event.Events;
import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.payment.application.dto.ConfirmTossPayment;
import com.zts.delivery.payment.application.dto.PaymentDoneEvent;
import com.zts.delivery.payment.domain.Payment;
import com.zts.delivery.payment.domain.PaymentRepository;
import com.zts.delivery.payment.domain.PaymentStatus;
import com.zts.delivery.payment.domain.PaymentType;
import com.zts.delivery.payment.infrastructure.client.TossPaymentConfirmClient;
import com.zts.delivery.payment.infrastructure.client.TossPaymentConfirmClientRequest;
import com.zts.delivery.payment.infrastructure.client.TossPaymentConfirmClientResponse;
import com.zts.delivery.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TossConfirmService {

    private final TossPaymentConfirmClient confirmClient;
    private final PaymentRepository paymentRepository;

    public void confirm(ConfirmTossPayment confirmTossPayment) {
        TossPaymentConfirmClientResponse response = requestConfirmation(confirmTossPayment);
        Payment payment = createConfirmedPayment(confirmTossPayment, response);
        paymentRepository.save(payment);

        Events.trigger(new PaymentDoneEvent(payment.getOrderId()));
    }

    private TossPaymentConfirmClientResponse requestConfirmation(ConfirmTossPayment confirmTossPayment) {
        TossPaymentConfirmClientRequest request = TossPaymentConfirmClientRequest
                .builder()
                .orderId(confirmTossPayment.orderId())
                .amount(confirmTossPayment.amount())
                .paymentKey(confirmTossPayment.paymentKey())
                .build();
        return confirmClient.confirm(request);
    }

    private Payment createConfirmedPayment(ConfirmTossPayment confirmTossPayment, TossPaymentConfirmClientResponse response) {
        return Payment.builder()
                .pgTransactionKey(response.paymentKey())
                .orderId(OrderId.of(UUID.fromString(response.orderId())))
                .totalPrice(new Price(response.totalAmount()))
                .requestedAt(response.requestedAt().toLocalDateTime())
                .approvedAt(response.approvedAt().toLocalDateTime())
                .userId(UserId.of(confirmTossPayment.userId()))
                .status(PaymentStatus.DONE)
                .type(PaymentType.TOSS)
                .build();
    }
}
