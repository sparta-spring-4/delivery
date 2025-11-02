package com.zts.delivery.user.presentation.dto;

import com.zts.delivery.user.presentation.vaidation.Phone;
import jakarta.validation.constraints.Email;

public record UserUpdateRequest(
        String firstName,
        String lastName,
        @Email
        String email,
        @Phone
        String phone
) {}
