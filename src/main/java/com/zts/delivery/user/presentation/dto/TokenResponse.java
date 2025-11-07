package com.zts.delivery.user.presentation.dto;


import lombok.Builder;

@Builder
public record TokenResponse(
        String accessToken,
        int expiresIn,
        int refreshExpiresIn,
        String refreshToken,
        String tokenType
) {
}
