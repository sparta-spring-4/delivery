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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Toss payment 승인/취소 API", description = "Toss payment 승인 / 취소 기능을 제공합니다.")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments/toss")
public class TossPaymentController {

    private final TossConfirmService tossConfirmService;

    private final TossCancelService tossCancelService;

    @Operation(summary = "결제 승인", description = "결제가 인증된 후 결제를 승인합니다.")
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

    @Operation(summary = "결제 취소", description = "결제가 완료된 후 결제를 취소 할 수 있습니다.")
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
