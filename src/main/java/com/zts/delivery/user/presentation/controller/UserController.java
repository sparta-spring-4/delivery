package com.zts.delivery.user.presentation.controller;

import com.zts.delivery.user.application.dto.*;
import com.zts.delivery.user.application.service.TokenGenerateService;
import com.zts.delivery.user.application.service.UserService;
import com.zts.delivery.user.domain.UserId;
import com.zts.delivery.user.infrastructure.security.UserPrincipal;
import com.zts.delivery.user.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Tag(name = "회원 API", description = "회원 가입 / 탈퇴 / 조회 / 변경 기능을 제공합니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final TokenGenerateService tokenService;
    private final UserService userService;

    @Operation(summary = "회원 가입", description = "회원 가입합니다.")
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

    @Operation(summary = "프로필 업데이트", description = "내 프로필 정보를 변경합니다.")
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

    @Operation(summary = "비밀번호 변경", description = "비밀번호를 변경합니다.")
    @PatchMapping("password")
    public void changePassword(@AuthenticationPrincipal UserPrincipal user, @Valid @RequestBody PasswordChangeRequest req) {
        userService.updatePassword(user.userId(), req.password());
    }

    @Operation(summary = "토근 발급", description = "username/password로 토큰을 발급합니다.")
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

    @Operation(summary = "프로필 조회", description = "내 프로필 정보를 조회합니다.")
    @GetMapping("/profile")
    public UserResponse getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        UserProfile userProfile = userService.getUserProfile(principal.userId());
        return UserResponse.of(userProfile);
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴합니다.")
    @DeleteMapping
    public void withdraw(@AuthenticationPrincipal UserPrincipal principal) {
        userService.withdraw(principal.userId());
    }

    @Operation(summary = "주소 추가", description = "주소를 추가합니다.")
    @PostMapping("/address")
    public List<UserAddressResponse> addUserAddress(@AuthenticationPrincipal UserPrincipal principal, @RequestBody RegisterUserAddressRequest address) {
        List<UserAddressInfo> userAddressInfos = userService.addUserAddresses(principal.userId(), address.toServiceDto());
        return userAddressInfos.stream()
                .map(UserAddressResponse::of)
                .toList();
    }

    @Operation(summary = "주소 삭제", description = "주소ID로 주소를 삭제합니다.")
    @DeleteMapping("/address/{addressId}")
    public void deleteUserAddress(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID addressId) {
        userService.deleteUserAddress(principal.userId(), addressId);
    }

    @Operation(summary = "프로필 조회", description = "userId 프로필을 조회합니다.(ROLE_MANAGER 이상 가능)")
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/profile/{userId}")
    public UserResponse userProfile(@PathVariable("userId") String userId) {
        UserProfile userProfile = userService.getUserProfile(UserId.of(userId));
        return UserResponse.of(userProfile);
    }

    @Operation(summary = "ROLE 변경", description = "ROLE을 변경합니다.(ROLE_MANAGER 이상 가능)")
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/change-role")
    public void changeRole(@RequestBody ChangeUserRoleRequest req) {
        ChangeUserRole changeUserRole = req.toServiceDto();
        userService.changeUserRole(changeUserRole);
    }
}
