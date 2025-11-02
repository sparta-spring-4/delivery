package com.zts.delivery.user.presentation.vaidation;

import com.zts.delivery.user.presentation.dto.UserRegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserRegisterRequest> {

    @Override
    public boolean isValid(UserRegisterRequest dto, ConstraintValidatorContext context) {
        String password = dto.password();
        String confirmedPassword = dto.confirmedPassword();

        if (password == null || confirmedPassword == null) {
            return false;
        }

        return password.equals(confirmedPassword);
    }
}