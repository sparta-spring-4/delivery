package com.zts.delivery.user.test;

import com.zts.delivery.user.infrastructure.security.JwtToUserAuthenticationConverter;
import com.zts.delivery.user.infrastructure.security.JwtToUserRoleConverter;
import com.zts.delivery.user.infrastructure.security.UserAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.Instant;
import java.util.*;

public class WithMockUserSecurityContextFactory implements WithSecurityContextFactory<MockUser> {
    @Override
    public SecurityContext createSecurityContext(MockUser anno) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", anno.userId());
        claims.put("preferred_username", anno.username());
        claims.put("client_id", anno.clientId());
        claims.put("email", anno.email());
        claims.put("name", anno.name());
        claims.put("phone", anno.phone());
        claims.put("iss", anno.issuer());
        Map<String, Object> realmAccess = Map.of("roles", Arrays.stream(anno.roles()).map(s -> "ROLE_" + s).toList());
        claims.put("realm_access", realmAccess);

        Instant now = Instant.now();
        Instant issuedAt = anno.issuedAt() > 0 ? Instant.ofEpochSecond(anno.issuedAt()) : now;
        Instant expiresAt = anno.expiresAt() > 0 ? issuedAt.plusSeconds(anno.expiresAt()) : now.plusSeconds(3600);

        Jwt jwt = new Jwt(
                "token-" + UUID.randomUUID(),
                issuedAt,
                expiresAt,
                Map.of("alg", "none"), // headers
                claims
        );

        JwtToUserAuthenticationConverter conv =  new JwtToUserAuthenticationConverter(new JwtToUserRoleConverter());
        UserAuthenticationToken userAuthenticationToken = conv.convert(jwt);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(userAuthenticationToken); // 로그인 처리

        return context;
    }
}
