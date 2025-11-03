package com.zts.delivery.user;

import com.zts.delivery.infrastructure.keycloak.TokenGenerateService;
import com.zts.delivery.infrastructure.keycloak.TokenInfo;
import com.zts.delivery.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final TokenGenerateService tokenService;

    @PostMapping("token")
    public TokenResponse generateToken(@Valid @RequestBody TokenRequest req) {
        TokenInfo tokenInfo = tokenService.generate(req.userId(), req.password());
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
                .id(principal.id())
                .userId(principal.userId())
                .name(principal.name())
                .email(principal.email())
                .build();
    }
}
