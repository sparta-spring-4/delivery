package com.zts.delivery.user.presentation.controller;

import com.zts.delivery.user.application.dto.TokenInfo;
import com.zts.delivery.user.application.dto.UserProfile;
import com.zts.delivery.user.application.dto.UserRegister;
import com.zts.delivery.user.application.dto.UserUpdate;
import com.zts.delivery.user.application.service.TokenGenerateService;
import com.zts.delivery.user.application.service.UserService;
import com.zts.delivery.user.domain.UserId;
import com.zts.delivery.user.infrastructure.security.UserPrincipal;
import com.zts.delivery.user.presentation.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final TokenGenerateService tokenService;
    private final UserService userService;

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
        userService.register(dto, LocalDateTime.now());
    }

    @PatchMapping("profile")
    public UserResponse updateProfile(@AuthenticationPrincipal UserPrincipal user, @Valid @RequestBody UserUpdateRequest req) {
        UserUpdate dto = UserUpdate
                .builder()
                .email(req.email())
                .firstName(req.firstName())
                .lastName(req.lastName())
                .phone(req.phone())
                .build();
        UserProfile userProfile = userService.update(user.userId(), dto, LocalDateTime.now());
        return UserResponse.of(userProfile);
    }

    @PatchMapping("password")
    public void changePassword(@AuthenticationPrincipal UserPrincipal user, @Valid @RequestBody PasswordChangeRequest req) {
        userService.updatePassword(user.userId(), req.password());
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
        UserProfile userProfile = userService.getUserProfile(principal.userId());
        return UserResponse.of(userProfile);
    }

    @DeleteMapping
    public void withdraw(@AuthenticationPrincipal UserPrincipal principal) {
        userService.withdraw(UserId.of(principal.userId()));
    }

    @PostMapping("/address")
    public UserResponse addUserAddress(@AuthenticationPrincipal UserPrincipal principal, @RequestBody RegisterUserAddressRequest address) {
        UserProfile userProfile = userService.addUserAddresses(UserId.of(principal.userId()), address.toServiceDto());
        return UserResponse.of(userProfile);
    }

    @DeleteMapping("/address/{addressId}")
    public void deleteUserAddress(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID addressId) {
        userService.deleteUserAddress(UserId.of(principal.userId()), addressId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/profile/{userId}")
    public UserResponse userProfile(@PathVariable("userId") String userId) {
        UserProfile userProfile = userService.getUserProfile(UUID.fromString(userId));
        return UserResponse.of(userProfile);
    }
}
