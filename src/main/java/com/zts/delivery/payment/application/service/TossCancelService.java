package com.zts.delivery.payment.application.service;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.infrastructure.event.Events;
import com.zts.delivery.infrastructure.execption.ApplicationException;
import com.zts.delivery.infrastructure.execption.ErrorCode;
import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.payment.application.dto.CancelTossPayment;
import com.zts.delivery.payment.application.dto.PaymentCancelDoneEvent;
import com.zts.delivery.payment.application.dto.PaymentFailLogEvent;
import com.zts.delivery.payment.domain.Payment;
import com.zts.delivery.payment.domain.PaymentMethod;
import com.zts.delivery.payment.domain.PaymentType;
import com.zts.delivery.payment.domain.repository.PaymentRepository;
import com.zts.delivery.payment.infrastructure.client.TossClientErrorException;
import com.zts.delivery.payment.infrastructure.client.TossPaymentClientResponse;
import com.zts.delivery.payment.infrastructure.client.cancel.TossPaymentCancelClient;
import com.zts.delivery.payment.infrastructure.client.cancel.TossPaymentCancelClientRequest;
import com.zts.delivery.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TossCancelService {

    private final TossPaymentCancelClient cancelClient;
    private final PaymentRepository paymentRepository;

    public void cancel(CancelTossPayment cancelTossPayment) {
        log.info("결제 취소 시작 (orderId: {})", cancelTossPayment.orderId().getId());

        Payment payment = paymentRepository.findByOrderId(cancelTossPayment.orderId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, "Not Found Payment (OrderId=" + cancelTossPayment.orderId().getId() + ")"));

        TossPaymentClientResponse cancelClientResponse = null;
        try {
            cancelClientResponse = cancelClient.cancel(new TossPaymentCancelClientRequest(payment.getPaymentKey(),
                    cancelTossPayment.cancelReason(), cancelTossPayment.cancelAmount().getValue()));
        } catch (TossClientErrorException e) {
            PaymentFailLogEvent cancelFailLogEvent = createCancelFailLogEvent(payment.getUserId(), payment.getOrderId(),
                    cancelTossPayment.cancelAmount(), payment.getPaymentKey(), cancelTossPayment.cancelReason(), e);
            Events.trigger(cancelFailLogEvent);
            throw e;
        }
        payment.cancel(cancelClientResponse.cancels().getFirst().canceledAt().toLocalDateTime());
        paymentRepository.saveAndFlush(payment);

        log.info("결제 취소 완료 (orderId: {})", cancelTossPayment.orderId().getId());

        Events.trigger(new PaymentCancelDoneEvent(payment.getOrderId()));
    }

    private PaymentFailLogEvent createCancelFailLogEvent(UserId userId, OrderId orderId,
                                                         Price refundAmount,
                                                         String paymentKey,
                                                         String cancelReason,
                                                         TossClientErrorException e) {
        return PaymentFailLogEvent.builder()
                .orderId(orderId)
                .userId(userId)
                .paymentKey(paymentKey)
                .paymentType(PaymentType.TOSS)
                .paymentMethod(PaymentMethod.CANCEL)
                .totalPrice(refundAmount)
                .cancelReason(cancelReason)
                .httpStatus((HttpStatus) e.getStatusCode())
                .errorCode(e.getCode())
                .errorMessage(e.getMessage())
                .erroredAt(LocalDateTime.now())
                .cancelReason(cancelReason)
                .build();
    }
}
