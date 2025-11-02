package com.zts.delivery.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class User {

    @Setter
    private UserId id;

    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private String phone;

    private String email;

    private List<Role> roles;

}
