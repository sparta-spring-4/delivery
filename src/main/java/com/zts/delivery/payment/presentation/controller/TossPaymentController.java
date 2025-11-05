package com.zts.delivery.payment.presentation.controller;

import com.zts.delivery.payment.application.dto.ConfirmTossPayment;
import com.zts.delivery.payment.application.service.TossConfirmService;
import com.zts.delivery.payment.infrastructure.client.TossPaymentConfirmClientRequest;
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

    @PostMapping("/confirm")
    public void confirm(@AuthenticationPrincipal UserPrincipal user,
                        @RequestBody TossPaymentConfirmClientRequest req) {
        ConfirmTossPayment confirmTossPayment = ConfirmTossPayment.builder()
                .orderId(req.orderId())
                .paymentKey(req.paymentKey())
                .amount(req.amount())
                .userId(user.userId().getId().toString())
                .build();

        tossConfirmService.confirm(confirmTossPayment);
    }
}
