package com.zts.delivery.user.presentation.controller;

import com.zts.delivery.user.application.dto.UserRegister;
import com.zts.delivery.user.application.dto.UserUpdate;
import com.zts.delivery.user.application.service.UserRegisterService;
import com.zts.delivery.user.application.service.UserUpdateService;
import com.zts.delivery.user.presentation.dto.*;
import com.zts.delivery.user.application.service.TokenGenerateService;
import com.zts.delivery.user.application.dto.TokenInfo;
import com.zts.delivery.user.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final TokenGenerateService tokenService;
    private final UserRegisterService registerService;
    private final UserUpdateService updateService;

    @PostMapping("signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@Valid @RequestBody UserRegisterRequest req) {
        UserRegister dto = UserRegister.builder()
                .username(req.username())
                .password(req.password())
                .email(req.email())
                .firstName(req.firstName())
                .lastName(req.lastName())
                .phone(req.phone())
                .build();
        registerService.register(dto);
    }

    @PatchMapping("profile")
    public void updateProfile(@AuthenticationPrincipal UserPrincipal user, @Valid @RequestBody UserUpdateRequest req) {
        UserUpdate dto = UserUpdate
                .builder()
                .email(req.email())
                .firstName(req.firstName())
                .lastName(req.lastName())
                .phone(req.phone())
                .build();
        updateService.update(user.userId(), dto);
    }


    @PostMapping("token")
    public TokenResponse generateToken(@Valid @RequestBody TokenRequest req) {
        TokenInfo tokenInfo = tokenService.generate(req.username(), req.password());
        return TokenResponse.builder()
                .accessToken(tokenInfo.accessToken())
                .expiresIn(tokenInfo.expiresIn())
                .refreshToken(tokenInfo.refreshToken())
                .refreshExpiresIn(tokenInfo.refreshExpiresIn())
                .tokenType(tokenInfo.tokenType())
                .build();
    }

    @GetMapping("/profile")
    public UserResponse getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return UserResponse
                .builder()
                .userId(principal.userId())
                .username(principal.username())
                .name(principal.name())
                .email(principal.email())
                .build();
    }
}
