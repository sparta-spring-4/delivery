package com.zts.delivery.user;

public interface TokenGenerateService {
    TokenInfo generate(String username, String password);
}