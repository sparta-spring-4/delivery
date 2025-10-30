package com.zts.delivery.infrastructure.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserAuthenticationToken extends AbstractAuthenticationToken {

    private final UserPrincipal principal;

    public UserAuthenticationToken(Collection<? extends GrantedAuthority> authorities, UserPrincipal principal, boolean authenticated) {
        super(authorities);
        this.principal = principal;
        setAuthenticated(authenticated);
    }

    public static UserAuthenticationToken authenticated(Collection<? extends GrantedAuthority> authorities, UserPrincipal principal) {
        return new UserAuthenticationToken(authorities, principal, true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
