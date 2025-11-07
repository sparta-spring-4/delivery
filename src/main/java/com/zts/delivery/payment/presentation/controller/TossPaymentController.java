package com.zts.delivery.payment.presentation.controller;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.payment.application.dto.CancelTossPayment;
import com.zts.delivery.payment.application.dto.ConfirmTossPayment;
import com.zts.delivery.payment.application.service.TossCancelService;
import com.zts.delivery.payment.application.service.TossConfirmService;
import com.zts.delivery.payment.presentation.dto.CancelTossPaymentRequest;
import com.zts.delivery.payment.presentation.dto.TossPaymentConfirmRequest;
import com.zts.delivery.user.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments/toss")
public class TossPaymentController {

    private final TossConfirmService tossConfirmService;

    private final TossCancelService tossCancelService;

    @PostMapping("/confirm")
    public void confirm(@AuthenticationPrincipal UserPrincipal user,
                        @RequestBody TossPaymentConfirmRequest req) {
        ConfirmTossPayment confirmTossPayment = ConfirmTossPayment.builder()
                .orderId(OrderId.of(req.orderId()))
                .paymentKey(req.paymentKey())
                .amount(new Price(req.amount()))
                .build();

        tossConfirmService.confirm(user.userId(), confirmTossPayment);
    }

    @PostMapping("/cancel")
    public void cancel(@RequestBody CancelTossPaymentRequest req) {
        CancelTossPayment cancelTossPayment = CancelTossPayment.builder()
                .orderId(OrderId.of(req.orderId()))
                .cancelAmount(new Price(req.cancelAmount()))
                .cancelReason(req.cancelReason())
                .build();
        tossCancelService.cancel(cancelTossPayment);
    }
}
