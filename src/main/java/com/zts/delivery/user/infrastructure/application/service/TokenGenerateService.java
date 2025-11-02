package com.zts.delivery.user.infrastructure.application.service;

import com.zts.delivery.user.infrastructure.application.dto.TokenInfo;

public interface TokenGenerateService {
    TokenInfo generate(String userId, String password);
}