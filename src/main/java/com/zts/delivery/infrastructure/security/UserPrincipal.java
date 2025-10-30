package com.zts.delivery.infrastructure.security;

import java.util.UUID;

public record UserPrincipal(
        UUID id,
        String userId,
        String email,
        String name) {
}