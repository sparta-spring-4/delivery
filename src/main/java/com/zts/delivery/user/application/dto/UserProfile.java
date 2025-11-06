package com.zts.delivery.user.application.dto;

import com.zts.delivery.user.domain.User;
import com.zts.delivery.user.domain.UserRole;
import com.zts.delivery.user.domain.UserStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record UserProfile(
        UUID userId,
        String username,
        String email,
        String firstName,
        String lastName,
        String phone,
        List<UserAddressInfo> addresses,
        List<UserRole> userRoles,
        UserStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserProfile of(User user) {
        List<UserAddressInfo> userAddressInfos = user.getAddresses().stream()
                .map(UserAddressInfo::of)
                .toList();

        return UserProfile.builder()
                .userId(user.getUserId().getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userRoles(user.getRoles())
                .phone(user.getPhone())
                .status(user.getStatus())
                .addresses(userAddressInfos)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
