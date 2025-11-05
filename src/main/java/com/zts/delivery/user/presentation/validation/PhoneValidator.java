package com.zts.delivery.user.presentation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private static final String PHONE_REGEX = "^(01[016])-(\\d{3,4})-(\\d{4})$";

    private static final Pattern PATTERN = Pattern.compile(PHONE_REGEX);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(value)) {
            return true;
        }

        return PATTERN.matcher(value).matches();
    }
}