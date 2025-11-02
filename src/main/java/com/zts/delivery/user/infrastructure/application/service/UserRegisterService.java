package com.zts.delivery.user.infrastructure.application.service;

import com.zts.delivery.infrastructure.execption.ApplicationException;
import com.zts.delivery.infrastructure.execption.ErrorCode;
import com.zts.delivery.user.domain.Role;
import com.zts.delivery.user.infrastructure.application.dto.UserRegister;
import com.zts.delivery.user.infrastructure.keycloak.KeycloakProperties;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(KeycloakProperties.class)
public class UserRegisterService {
    private final KeycloakProperties properties;
    private final Keycloak keycloak;

    public void register(UserRegister dto) {
        UsersResource usersResource = keycloak.realm(properties.getRealm()).users();

        checkDuplicatedUsername(usersResource, dto.username());
        checkDuplicatedEmail(usersResource, dto.email());

        UserRepresentation user = createUserRepresentation(dto);
        setUserAttribute(user, dto);

        Response response = usersResource.create(user);

        String userId = CreatedResponseUtil.getCreatedId(response);
        setPassword(usersResource, userId, dto.password());
        setRole(usersResource, userId, Role.USER);
    }

    private void checkDuplicatedUsername(UsersResource usersResource, String username) {
        List<UserRepresentation> existingUsersByUsername = usersResource.searchByUsername(username, true);
        if (!existingUsersByUsername.isEmpty()) {
            throw new ApplicationException(ErrorCode.DUPLICATED_USERNAME);
        }
    }

    private void checkDuplicatedEmail(UsersResource usersResource, String email) {
        List<UserRepresentation> existingUsersByEmail = usersResource.searchByEmail(email, true);
        if (!existingUsersByEmail.isEmpty()) {
            throw new ApplicationException(ErrorCode.DUPLICATED_EMAIL);
        }
    }

    private UserRepresentation createUserRepresentation(UserRegister dto) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        return user;
    }

    private void setUserAttribute(UserRepresentation user, UserRegister dto) {
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("phone", List.of(dto.phone()));
        user.setAttributes(attributes);
    }

    private void setPassword(UsersResource usersResource, String userId, String password) {
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);
        usersResource.get(userId).resetPassword(passwordCred);
    }

    private void setRole(UsersResource usersResource, String userId, Role role) {
        RoleRepresentation userRole = keycloak.realm(properties.getRealm()).roles().get("ROLE_" + role.name()).toRepresentation();
        usersResource.get(userId).roles().realmLevel().add(List.of(userRole));
    }
}