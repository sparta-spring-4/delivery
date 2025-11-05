package com.zts.delivery.user.infrastructure.security;

import com.zts.delivery.user.domain.UserId;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserPrincipal(
        UserId userId,
        String username,
        String email,
        String name) {
}