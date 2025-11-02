package com.zts.delivery.user.application.service;

import com.zts.delivery.infrastructure.execption.ApplicationException;
import com.zts.delivery.infrastructure.execption.ErrorCode;
import com.zts.delivery.user.application.dto.UserProfile;
import com.zts.delivery.user.infrastructure.keycloak.KeycloakProperties;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(KeycloakProperties.class)
public class UserProfileService {

    private final KeycloakProperties properties;
    private final Keycloak keycloak;

    public UserProfile getUserProfile(UUID userId) {
        try {
            UserRepresentation representation = keycloak.realm(properties.getRealm()).users().get(userId.toString()).toRepresentation();
            return UserProfile.builder()
                    .username(representation.getUsername())
                    .userId(UUID.fromString(representation.getId()))
                    .name(representation.getLastName() + representation.getFirstName())
                    .email(representation.getEmail())
                    .phone(representation.getAttributes().get("phone").getFirst())
                    .build();
        } catch (NotFoundException e) {
            throw new ApplicationException(ErrorCode.USER_NOT_FOUND);
        }
    }
}
