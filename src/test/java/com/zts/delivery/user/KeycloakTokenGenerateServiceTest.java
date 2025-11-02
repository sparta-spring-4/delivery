package com.zts.delivery.user;

import com.zts.delivery.user.application.service.TokenGenerateService;
import com.zts.delivery.user.application.dto.TokenInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KeycloakTokenGenerateServiceTest {

    @Autowired
    TokenGenerateService tokenGenerateService;

    @Test
    void generateToken() {
        TokenInfo token = tokenGenerateService.generate("jwkim.oa", "1111");
        System.out.println(token);
    }
}

