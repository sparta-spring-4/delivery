package com.zts.delivery.user.presentation.dto;

import com.zts.delivery.user.application.dto.UserProfile;
import com.zts.delivery.user.domain.UserStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserResponse(
        UUID userId,
        String username,
        String email,
        String name,
        String phone,
        UserStatus status,
        LocalDateTime createdAt
) {
    public static UserResponse of(UserProfile userProfile) {
        return UserResponse.builder()
                .userId(userProfile.userId())
                .username(userProfile.username())
                .name(userProfile.lastName() + " " + userProfile.firstName())
                .email(userProfile.email())
                .phone(userProfile.phone())
                .status(userProfile.status())
                .createdAt(userProfile.createdAt())
                .build();
    }
}