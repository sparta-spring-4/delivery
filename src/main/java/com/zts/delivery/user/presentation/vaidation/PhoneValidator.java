package com.zts.delivery.user.presentation.vaidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private static final String PHONE_REGEX = "^(01[016])-(\\d{3,4})-(\\d{4})$";
    private Pattern pattern;

    @Override
    public void initialize(Phone constraintAnnotation) {
        this.pattern = Pattern.compile(PHONE_REGEX);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return pattern.matcher(value).matches();
    }
}