package com.zts.delivery.user.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserRole {
    CUSTOMER("고객"),
    STAFF("Store 직원"),
    OWNER("Store 사장"),
    MANAGER("관리자"),
    MASTER("마스터(모든 권한을 가진다.)");

    private final String description;
}
