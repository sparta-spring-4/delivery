package com.zts.delivery.user.application.service;

import com.zts.delivery.user.application.dto.TokenInfo;

public interface TokenGenerateService {
    TokenInfo generate(String userId, String password);
}