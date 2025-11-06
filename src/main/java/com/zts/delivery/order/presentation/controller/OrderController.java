package com.zts.delivery.order.presentation.controller;


import com.zts.delivery.order.application.OrderService;
import com.zts.delivery.order.presentation.dto.OrderRequest;
import com.zts.delivery.order.presentation.dto.OrderResponse;
import com.zts.delivery.user.infrastructure.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/order")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문하기", description = "장바구니의 상품들을 주문합니다.")
    @PostMapping
    public OrderResponse createOrder(@RequestBody @Valid OrderRequest req, @AuthenticationPrincipal UserPrincipal user) {
        return orderService.create(req, user.userId());
    }
}
