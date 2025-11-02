package com.zts.delivery.user.presentation.vaidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Phone {
    // 기본 오류 메시지
    String message() default "Invalid mobile phone number format. (e.g., 010-1234-5678)";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}