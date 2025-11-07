package com.zts.delivery.payment.application.service;

import com.zts.delivery.infrastructure.event.Events;
import com.zts.delivery.payment.application.dto.CancelTossPayment;
import com.zts.delivery.payment.application.dto.ConfirmTossPayment;
import com.zts.delivery.payment.application.dto.PaymentCancelFailEvent;
import com.zts.delivery.payment.application.dto.PaymentConfirmFailEvent;
import com.zts.delivery.payment.domain.PaymentLog;
import com.zts.delivery.payment.domain.PaymentMethod;
import com.zts.delivery.payment.domain.PaymentType;
import com.zts.delivery.payment.domain.repository.PaymentLogRepository;
import com.zts.delivery.payment.infrastructure.client.TossClientErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentRetryScheduler {

    private final PaymentLogRepository paymentLogRepository;
    private final TossConfirmService tossConfirmService;
    private final TossCancelService tossCancelService;

    private static final int MAX_RETRY_COUNT = 2;
    private static final int LOG_FIND_PAGE_SIZE = 10;

    /**
     * 실패한 결제 재시도
     * - 2번 재시도 실패 시 -> 주문 취소
     */
    @Scheduled(fixedDelay = 1L, timeUnit = TimeUnit.SECONDS)
    public void retryConfirmPayment() {
        List<PaymentLog> failLogs = paymentLogRepository.findFailLogs(PaymentType.TOSS,
                PaymentMethod.CONFIRM, MAX_RETRY_COUNT,
                false,
                PageRequest.of(0, LOG_FIND_PAGE_SIZE));

        for (PaymentLog failLog : failLogs) {
            processSingleConfirmRetry(failLog);
        }
        paymentLogRepository.saveAllAndFlush(failLogs);
    }

    /**
     * 실패한 결제 재시도
     * - 2번 재시도 실패 시
     */
    @Scheduled(fixedDelay = 1L, timeUnit = TimeUnit.SECONDS)
    public void retryCancelPayment() {
        List<PaymentLog> failLogs = paymentLogRepository.findFailLogs(PaymentType.TOSS,
                PaymentMethod.CANCEL, MAX_RETRY_COUNT,
                false,
                PageRequest.of(0, LOG_FIND_PAGE_SIZE));
        for (PaymentLog failLog : failLogs) {
            processSingleCancelRetry(failLog);
        }
        paymentLogRepository.saveAllAndFlush(failLogs);
    }

    private void processSingleCancelRetry(PaymentLog failLog) {
        try {
            tossCancelService.cancel(new CancelTossPayment(failLog.getOrderId(), failLog.getTotalPrice(), failLog.getCancelReason()));
        } catch (TossClientErrorException e) {
            log.warn("결제 취소 재시도 실패 (orderId: {})", failLog.getOrderId());
            if (failLog.isMaxRetried(MAX_RETRY_COUNT)) {
                Events.trigger(new PaymentCancelFailEvent(failLog.getOrderId()));
            }
            return;
        }
        log.info("결제 취소 재시도 성공 (orderId: {})", failLog.getOrderId());
        failLog.success();
    }

    private void processSingleConfirmRetry(PaymentLog failLog) {
        try {
            tossConfirmService.confirm(failLog.getUserId(), new ConfirmTossPayment(failLog.getOrderId(), failLog.getPaymentKey(), failLog.getTotalPrice()));
        } catch (TossClientErrorException e) {
            log.warn("결제 승인 재시도 실패 (orderId: {})", failLog.getOrderId());
            if (failLog.isMaxRetried(MAX_RETRY_COUNT)) {
                Events.trigger(new PaymentConfirmFailEvent(failLog.getOrderId()));
            }
            return;
        }
        log.info("결제 승인 재시도 성공 (orderId: {})", failLog.getOrderId());
        failLog.success();
    }
}
