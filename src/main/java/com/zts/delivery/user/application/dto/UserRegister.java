package com.zts.delivery.user.application.dto;

import com.zts.delivery.user.domain.UserRole;
import com.zts.delivery.user.domain.User;
import com.zts.delivery.user.domain.UserStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record UserRegister(
        String username,
        String password,
        String email,
        String firstName,
        String lastName,
        String phone
) {

    public User toUser(List<UserRole> roles, LocalDateTime registeredAt) {
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .roles(roles)
                .status(UserStatus.ACTIVE)
                .createdAt(registeredAt)
                .updatedAt(registeredAt)
                .build();
    }

}