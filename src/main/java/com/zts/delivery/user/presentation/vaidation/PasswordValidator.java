package com.zts.delivery.user.presentation.vaidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    // 정규식: 문자, 숫자, 특수 문자 포함, 8자~12자 모두 충족
    // 1. (?=.*[a-zA-Z]): 최소 1개의 문자(대/소문자) 포함
    // 2. (?=.*\d): 최소 1개의 숫자 포함
    // 3. (?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>/?]): 최소 1개의 특수 문자 포함
    // 4. .{8, 12}: 8자 이상, 12자 이하
    private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,12}$";

    private Pattern pattern;


    @Override
    public void initialize(Password constraintAnnotation) {
        this.pattern = Pattern.compile(PASSWORD_REGEX);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(value)) {
            return true;
        }

        return pattern.matcher(value).matches();
    }
}