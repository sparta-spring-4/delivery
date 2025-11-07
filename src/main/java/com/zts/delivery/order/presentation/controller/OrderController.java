package com.zts.delivery.order.presentation.controller;


import com.zts.delivery.order.application.DeliveryService;
import com.zts.delivery.order.application.OrderService;
import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.order.presentation.dto.OrderRequest;
import com.zts.delivery.order.presentation.dto.OrderResponse;
import com.zts.delivery.order.presentation.dto.OrderStatusChangeRequest;
import com.zts.delivery.user.infrastructure.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/order")
public class OrderController {

    private final OrderService orderService;
    private final DeliveryService deliveryService;

    @Operation(summary = "주문 생성", description = "장바구니의 상품들을 주문합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody @Valid OrderRequest req, @AuthenticationPrincipal UserPrincipal user) {
        return orderService.create(req, user.userId());
    }

    @Operation(summary = "주문 취소", description = "관리자가 주문을 취소합니다")
    @PatchMapping("/{orderId}/cancel")
    public void updateOrderStatus(@PathVariable OrderId orderId, @AuthenticationPrincipal UserPrincipal user) {
        orderService.cancel(orderId, user.userId());
    }

    @Operation(summary = "주문 수락", description = "매장에서 주문을 수락합니다.(임시로 배달 완료까지 이어집니다)")
    @PostMapping("/accept")
    public ResponseEntity<String> acceptOrder(@RequestBody @Valid OrderStatusChangeRequest req, @AuthenticationPrincipal UserPrincipal user) {
        orderService.acceptOrder(req.id());

        deliveryService.simulateDelivery(req.id());

        return ResponseEntity.ok("주문(ID: " + req.id() + ") 접수 완료. 배달 시뮬레이션을 시작합니다.");
    }

    @Operation(summary = "주문 거절", description = "매장에서 주문을 거절합니다.")
    @PostMapping("/{orderId}/reject")
    public ResponseEntity<String> rejectOrder(@PathVariable OrderId orderId, @AuthenticationPrincipal UserPrincipal user) {
        orderService.reject(orderId, user.userId());
        return ResponseEntity.ok("주문(ID: " + orderId + ")이 거절되었습니다.");
    }

    @Operation(summary = "주문 삭제", description = "주문 삭제")
    @PostMapping("/{orderId}/delete")
    public ResponseEntity<String> deleteOrder(@PathVariable OrderId orderId , @AuthenticationPrincipal UserPrincipal user) {
        orderService.delete(orderId, user);
        return ResponseEntity.ok("주문(ID: " + orderId + ")이 삭제되었습니다.");
    }

    @Operation(summary = "주문 조회", description = "주문 조회")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable OrderId orderId, @AuthenticationPrincipal UserPrincipal user) {
        OrderResponse response = orderService.read(orderId, user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "주문 목록 조회", description = "주문 목록 조회")
    @GetMapping("/list")
    public ResponseEntity<List<OrderResponse>> getOrderList(@AuthenticationPrincipal UserPrincipal user) {
        List<OrderResponse> response = orderService.readAll(user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
