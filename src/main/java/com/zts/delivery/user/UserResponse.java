package com.zts.delivery.user;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        String userId,
        String email,
        String name
) {}