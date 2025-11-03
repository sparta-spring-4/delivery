package com.zts.delivery.user;

import com.zts.delivery.infrastructure.keycloak.TokenGenerateService;
import com.zts.delivery.infrastructure.keycloak.TokenInfo;
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

