package com.zts.delivery.user.presentation.dto;

import com.zts.delivery.user.presentation.vaidation.PasswordMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@PasswordMatch
public record PasswordChangeRequest(
        @NotBlank
        @Size(min = 8)
        String password,

        @NotBlank
        String confirmedPassword
) implements PasswordContainer {
}