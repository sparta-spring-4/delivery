package com.zts.delivery.payment.application.service;

import com.zts.delivery.payment.application.dto.ConfirmTossPayment;
import com.zts.delivery.payment.domain.PaymentLog;
import com.zts.delivery.payment.domain.PaymentMethod;
import com.zts.delivery.payment.domain.PaymentType;
import com.zts.delivery.payment.domain.repository.PaymentLogRepository;
import com.zts.delivery.payment.infrastructure.client.TossClientErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentRetryScheduler {

    private final PaymentLogRepository paymentLogRepository;
    private final TossConfirmService tossConfirmService;
    private static final int RETRY_COUNT = 2;
    private static final int LOG_FIND_PAGE_SIZE = 10;

    /**
     * 실패한 결제 재시도
     * - 2번 재시도 실패 시 -> 주문 취소
     */
    @Scheduled(fixedDelay = 1L, timeUnit = TimeUnit.SECONDS)
    public void retryConfirmPayment() {
        Page<PaymentLog> failLogs = paymentLogRepository.findFailLogs(PaymentType.TOSS,
                PaymentMethod.CONFIRM, RETRY_COUNT,
                false,
                PageRequest.of(0, LOG_FIND_PAGE_SIZE));

        for (PaymentLog failLog : failLogs) {
            String paymentKey = failLog.getPaymentKey();
            int totalPrice = failLog.getTotalPrice().getValue();
            String orderId = failLog.getOrderId().getId().toString();
            try {
                tossConfirmService.confirm(failLog.getUserId(), new ConfirmTossPayment(orderId, paymentKey, totalPrice));
            } catch (TossClientErrorException e) {
                log.warn("Payment retry failed for orderId: {}", failLog.getOrderId());
                continue;
            }

            log.warn("success to retry for orderId: {}", failLog.getOrderId());
            failLog.success();
            paymentLogRepository.saveAllAndFlush(failLogs);
        }

    }

}
