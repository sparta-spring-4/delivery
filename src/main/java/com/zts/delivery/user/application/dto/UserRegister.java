package com.zts.delivery.user.application.dto;

import com.zts.delivery.user.domain.Role;
import com.zts.delivery.user.domain.User;
import lombok.Builder;

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

    public User toUser(List<Role> roles) {
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .roles(roles)
                .build();
    }

}