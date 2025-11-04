package com.zts.delivery.order.presentation.controller;
import com.zts.delivery.user.infrastructure.security.UserPrincipal;
import com.zts.delivery.order.application.CartService;
import com.zts.delivery.order.domain.cart.Cart;
import com.zts.delivery.order.domain.cart.CartItem;
import com.zts.delivery.order.domain.cart.CartRepository;
import com.zts.delivery.order.presentation.dto.CartRequest;
import com.zts.delivery.order.presentation.dto.CartResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/carts")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니 담기", description = "아이템id를 통해서 장바구니에 물건을 담습니다.")
    @PostMapping("/create")
    public CartResponse createCart(@RequestBody @Valid CartRequest req, @AuthenticationPrincipal UserPrincipal principal) {

        CartResponse response = cartService.addCartItem(req, principal.userId().getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response).getBody();
    }
}
