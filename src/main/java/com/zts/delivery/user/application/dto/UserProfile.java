package com.zts.delivery.user.application.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserProfile(
        UUID userId,
        String username,
        String email,
        String name,
        String phone
) {
}
