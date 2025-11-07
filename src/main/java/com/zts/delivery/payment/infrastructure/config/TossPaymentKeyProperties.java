package com.zts.delivery.payment.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "payment.toss")
public record TossPaymentKeyProperties(
        String secretKey,
        Confirm confirm,
        Cancel cancel

) {
    public record Confirm(String uri) {
    }

    public record Cancel(String uri) {
    }
}