package com.zts.delivery.user.infrastructure.security;


import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class JwtToUserAuthenticationConverter implements Converter<Jwt, UserAuthenticationToken> {

    private final Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter;

    public JwtToUserAuthenticationConverter(Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter) {
        this.authoritiesConverter = authoritiesConverter;
    }

    @Override
    public UserAuthenticationToken convert(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();

        String familyName = (String) claims.getOrDefault("family_name", "");
        String givenName = (String) claims.getOrDefault("given_name", "");
        String name = familyName + givenName;

        UserPrincipal principal = UserPrincipal.builder()
                .userId(UUID.fromString(jwt.getSubject()))
                .username((String) claims.getOrDefault("preferred_username", ""))
                .email((String) claims.getOrDefault("email", ""))
                .name(name)
                .build();

        Collection<GrantedAuthority> authorities = authoritiesConverter.convert(jwt);
        return UserAuthenticationToken.authenticated(authorities, principal);
    }
}
