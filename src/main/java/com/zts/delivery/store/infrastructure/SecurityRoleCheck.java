package com.zts.delivery.store.infrastructure;

import com.zts.delivery.store.domain.RoleCheck;
import com.zts.delivery.store.domain.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SecurityRoleCheck implements RoleCheck {

    // 매점 수정/삭제 권한
    @Override
    public boolean check(Store store) {
        if (store == null) return false;

        String role = SecurityRoleCheckHelper.getRole(store);
        return StringUtils.hasText(role); // ADMIN 또는 OWNER인 경우 권한 존재
    }
}
