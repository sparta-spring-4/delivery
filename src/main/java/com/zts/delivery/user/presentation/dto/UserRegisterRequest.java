package com.zts.delivery.user.presentation.dto;

import com.zts.delivery.user.presentation.validation.Password;
import com.zts.delivery.user.presentation.validation.PasswordMatch;
import com.zts.delivery.user.presentation.validation.Phone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@PasswordMatch
public record UserRegisterRequest(
        @NotBlank
        @Size(min = 4)
        String username,

        @NotBlank
        @Password
        String password,

        @NotBlank
        String confirmedPassword,

        @Email
        @NotBlank
        String email,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotBlank
        @Phone
        String phone
) implements PasswordContainer {
}
