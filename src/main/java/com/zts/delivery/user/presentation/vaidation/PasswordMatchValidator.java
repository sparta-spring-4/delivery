package com.zts.delivery.user.presentation.vaidation;

import com.zts.delivery.user.presentation.dto.PasswordContainer;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, PasswordContainer> {

    @Override
    public boolean isValid(PasswordContainer pc, ConstraintValidatorContext context) {
        String password = pc.password();
        String confirmedPassword = pc.confirmedPassword();

        if (!StringUtils.hasText(password) || !StringUtils.hasText(confirmedPassword)) {
            return true;
        }

        return password.equals(confirmedPassword);
    }
}