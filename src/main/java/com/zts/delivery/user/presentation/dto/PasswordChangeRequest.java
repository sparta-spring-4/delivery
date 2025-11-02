package com.zts.delivery.user.presentation.dto;

import com.zts.delivery.user.presentation.vaidation.Password;
import com.zts.delivery.user.presentation.vaidation.PasswordMatch;
import jakarta.validation.constraints.NotBlank;

@PasswordMatch
public record PasswordChangeRequest(
        @NotBlank
        @Password
        String password,

        @NotBlank
        String confirmedPassword
) implements PasswordContainer {
}