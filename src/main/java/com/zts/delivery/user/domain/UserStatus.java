package com.zts.delivery.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
    ACTIVE("활성"),
    BLOCKED("차단"),
    WITHDRAWN("탈퇴");

    private final String description;

}
