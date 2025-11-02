package com.zts.delivery.user.infrastructure.security;

import java.util.UUID;

public record UserPrincipal(
        UUID id,
        String userId,
        String email,
        String name) {
}