package com.zts.delivery.user.infrastructure.keycloak;

public interface TokenGenerateService {
    TokenInfo generate(String userId, String password);
}