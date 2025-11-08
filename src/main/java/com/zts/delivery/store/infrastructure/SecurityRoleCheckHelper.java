package com.zts.delivery.store.infrastructure;

import com.zts.delivery.store.domain.Store;
import com.zts.delivery.user.infrastructure.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
public class SecurityRoleCheckHelper {
    /**
     * 1. OWNER 권한 & 본인 매장의 정보를 변경하는 경우
     * 2. MASTER, MANAGER
     * @param store
     * @return
     */
    public static String getRole(Store store) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null &&  authentication.getPrincipal() instanceof UserPrincipal user) { // 로그인을 한 회원에 한정해서 확인
            UUID loggedUserId = user.userId().getId();       // 로그인 사용자 UUID
            UUID ownerId = store.getOwner().getId().getId(); // 매장 주인 UUID

            if (loggedUserId.equals(ownerId)) { // 매장 주인 여부
                return "OWNER";
            }

            // 관리자인지 (MASTER, MANAGER)
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean isAdmin = authorities == null ? false : authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_MASTER") || a.getAuthority().equals("ROLE_MANAGER"));
            if (isAdmin) return "ADMIN";

        }

        return ""; // 문자열이 비어 있으면 권한 X
    }
}
