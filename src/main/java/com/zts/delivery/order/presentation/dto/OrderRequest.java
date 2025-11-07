package com.zts.delivery.order.presentation.dto;

import java.util.UUID;
import lombok.Builder;

@Builder
public record OrderRequest(
    UUID cartId,                // 장바구니 식별자만 전달
    String ordererName,         // 주문자 이름
    String ordererPhone,        // 주문자 전화번호
    String address,
    String memo,
    double latitude,
    double longitude
) {}