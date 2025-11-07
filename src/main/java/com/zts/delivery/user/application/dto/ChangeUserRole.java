package com.zts.delivery.user.application.dto;

import com.zts.delivery.user.domain.UserId;
import com.zts.delivery.user.domain.UserRole;
import lombok.Builder;

@Builder
public record ChangeUserRole(
        UserId userId,
        UserRole role
) {
}
