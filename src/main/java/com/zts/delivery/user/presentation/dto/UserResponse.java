package com.zts.delivery.user.presentation.dto;

import com.zts.delivery.user.domain.UserStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserResponse(
        UUID userId,
        String username,
        String email,
        String name,
        String phone,
        UserStatus status,
        LocalDateTime createdAt
) {
}