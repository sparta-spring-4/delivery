package com.zts.delivery.user;

import jakarta.validation.constraints.NotBlank;

public record TokenRequest(
        @NotBlank
        String userId,
        @NotBlank
        String password
) {}
