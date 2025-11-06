package com.zts.delivery.order.presentation.controller;
import com.zts.delivery.order.presentation.dto.CartItemOptionUpdateRequest;
import com.zts.delivery.order.presentation.dto.CartUpdateRequest;
import com.zts.delivery.user.infrastructure.security.UserPrincipal;
import com.zts.delivery.order.application.CartService;
import com.zts.delivery.order.presentation.dto.CartRequest;
import com.zts.delivery.order.presentation.dto.CartResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/cart")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니 담기", description = "아이템id를 통해서 장바구니에 물건을 담습니다.")
    @PostMapping
    public CartResponse createCart(@RequestBody @Valid CartRequest req, @AuthenticationPrincipal UserPrincipal user) {
        log.info("Request: {}, UserId: {}", req, user.userId());

        CartResponse response = cartService.create(req, user.userId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response).getBody();
    }

    @Operation(summary = "장바구니 조회", description = "사용자의 장바구니를 조회합니다.")
    @GetMapping
    public CartResponse getCart(@AuthenticationPrincipal UserPrincipal user) {
        CartResponse response = cartService.read(user.userId());
        return ResponseEntity.status(HttpStatus.OK).body(response).getBody();
    }
    @Operation(summary = "장바구니 변경", description = "장바구니에 담긴 아이템 변경")
    @PatchMapping
    public CartResponse changeCartItemQuantity(@RequestBody @Valid CartUpdateRequest req, @AuthenticationPrincipal UserPrincipal user) {
        log.info("Request: {}, UserId: {}", req, user.userId());

        CartResponse response = cartService.update(req, user.userId());

        return ResponseEntity.status(HttpStatus.OK).body(response).getBody();
    }

    @Operation(summary = "장바구니 삭제", description = "사용자의 장바구니를 비웁니다.")
    @DeleteMapping
    public void deleteCart(@AuthenticationPrincipal UserPrincipal user) {
        cartService.delete(user.userId());
    }

    @Operation(summary = "장바구니 품목 옵션 변경", description = "장바구니에 담긴 아이템 옵션 변경")
    @PatchMapping("/options")
    public CartResponse changeCartItemOptions(@RequestBody @Valid CartItemOptionUpdateRequest req, @AuthenticationPrincipal UserPrincipal user) {
        log.info("Request: {}, UserId: {}", req, user.userId());
        CartResponse response = cartService.updateItemOptions(req, user.userId());
        return ResponseEntity.status(HttpStatus.OK).body(response).getBody();
    }

}
