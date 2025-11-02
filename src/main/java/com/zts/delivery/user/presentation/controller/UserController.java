package com.zts.delivery.user.presentation.controller;

import com.zts.delivery.user.infrastructure.application.dto.UserRegister;
import com.zts.delivery.user.infrastructure.application.service.UserRegisterService;
import com.zts.delivery.user.presentation.dto.UserRegisterRequest;
import com.zts.delivery.user.presentation.dto.UserResponse;
import com.zts.delivery.user.infrastructure.application.service.TokenGenerateService;
import com.zts.delivery.user.infrastructure.application.dto.TokenInfo;
import com.zts.delivery.user.infrastructure.security.UserPrincipal;
import com.zts.delivery.user.presentation.dto.TokenRequest;
import com.zts.delivery.user.presentation.dto.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final TokenGenerateService tokenService;
    private final UserRegisterService registerService;

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
