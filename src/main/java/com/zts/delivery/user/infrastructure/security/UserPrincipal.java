package com.zts.delivery.user.infrastructure.security;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserPrincipal(
        UUID userId,
        String username,
        String email,
        String name) {
}