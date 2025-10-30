package com.zts.delivery.infrastructure.keycloak;

public interface TokenGenerateService {
    TokenInfo generate(String userId, String password);
}