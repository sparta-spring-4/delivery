package com.zts.delivery.user.presentation.dto;

import com.zts.delivery.user.application.dto.ChangeUserRole;
import com.zts.delivery.user.domain.UserId;
import com.zts.delivery.user.domain.UserRole;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.UUID;

public record ChangeUserRoleRequest(
        @NotBlank
        UserRole role,

        @NotBlank
        @UUID
        String userId
) {
    public ChangeUserRole toServiceDto() {
        return ChangeUserRole.builder()
                .role(role)
                .userId(UserId.of(userId))
                .build();
    }
}
