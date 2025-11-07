package com.zts.delivery.user.presentation.controller;

import com.zts.delivery.user.application.dto.*;
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
import java.util.List;
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
        userService.withdraw(principal.userId());
    }

    @PostMapping("/address")
    public List<UserAddressResponse> addUserAddress(@AuthenticationPrincipal UserPrincipal principal, @RequestBody RegisterUserAddressRequest address) {
        List<UserAddressInfo> userAddressInfos = userService.addUserAddresses(principal.userId(), address.toServiceDto());
        return userAddressInfos.stream()
                .map(UserAddressResponse::of)
                .toList();
    }

    @DeleteMapping("/address/{addressId}")
    public void deleteUserAddress(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID addressId) {
        userService.deleteUserAddress(principal.userId(), addressId);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/profile/{userId}")
    public UserResponse userProfile(@PathVariable("userId") String userId) {
        UserProfile userProfile = userService.getUserProfile(UserId.of(userId));
        return UserResponse.of(userProfile);
    }
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/change-role")
    public void changeRole(@RequestBody ChangeUserRoleRequest req) {
        ChangeUserRole changeUserRole = req.toServiceDto();
        userService.changeUserRole(changeUserRole);
    }
}
