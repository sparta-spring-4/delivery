package com.zts.delivery.store.infrastructure;

import com.zts.delivery.store.application.service.StoreUser;
import com.zts.delivery.user.infrastructure.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityStoreUser implements StoreUser {
    @Override
    public String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return userPrincipal.username();
        }

        return null;
    }
}
