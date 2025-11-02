package com.zts.delivery.user.application.service;

import com.zts.delivery.infrastructure.execption.ApplicationException;
import com.zts.delivery.infrastructure.execption.ErrorCode;
import com.zts.delivery.user.application.dto.UserUpdate;
import com.zts.delivery.user.infrastructure.keycloak.KeycloakProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;


@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(KeycloakProperties.class)
public class UserUpdateService {

    private final KeycloakProperties properties;
    private final Keycloak keycloak;

    public void update(UUID userId, UserUpdate dto) {
        UserRepresentation user = getUserProfile(userId);
        updateFirstName(user, dto.firstName());
        updateLastName(user, dto.lastName());
        updateEmail(user, dto.email());
        updateAttributes(user, dto);
        keycloak.realm(properties.getRealm()).users().get(userId.toString()).update(user);
    }

    public void updatePassword(UUID userId, String newPassword) {
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(newPassword);
        keycloak.realm(properties.getRealm()).users().get(userId.toString()).resetPassword(passwordCred);
    }

    private void updateFirstName(UserRepresentation user, String firstName) {
        if (StringUtils.hasText(firstName)) {
            user.setFirstName(firstName);
        }
    }

    private void updateLastName(UserRepresentation user, String lastName) {
        if (StringUtils.hasText(lastName)) {
            user.setLastName(lastName);
        }
    }

    private void updateEmail(UserRepresentation user, String email) {
        if (StringUtils.hasText(email)) {
            if (!user.getEmail().equals(email)) {
                checkDuplicatedEmail(email);
            }
            user.setEmail(email);
        }
    }

    private void checkDuplicatedEmail(String email) {
        UsersResource usersResource = keycloak.realm(properties.getRealm()).users();
        List<UserRepresentation> existingUsersByEmail = usersResource.searchByEmail(email, true);
        if (!existingUsersByEmail.isEmpty()) {
            throw new ApplicationException(ErrorCode.DUPLICATED_EMAIL);
        }
    }

    private void updateAttributes(UserRepresentation user, UserUpdate dto) {
        Map<String, List<String>> attributes = Objects.requireNonNullElseGet(user.getAttributes(), HashMap::new);

        if (StringUtils.hasText(dto.phone())) {
            attributes.put("phone", List.of(dto.phone()));
        }
        user.setAttributes(attributes);
    }

    private UserRepresentation getUserProfile(UUID userId) {
        return keycloak.realm(properties.getRealm()).users().get(userId.toString()).toRepresentation();
    }
}
