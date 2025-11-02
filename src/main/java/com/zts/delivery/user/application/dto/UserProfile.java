package com.zts.delivery.user.application.dto;

import com.zts.delivery.user.domain.User;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserProfile(
        UUID userId,
        String username,
        String email,
        String name,
        String phone
) {
    public static UserProfile of(User user) {
        return UserProfile.builder()
                .userId(user.getId().getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getLastName() + " " + user.getFirstName())
                .phone(user.getPhone())
                .build();
    }
}
