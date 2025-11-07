package com.zts.delivery.user.domain.repository;

import com.zts.delivery.user.domain.*;
import com.zts.delivery.user.domain.converter.UserAddressListToJsonListConverter;
import com.zts.delivery.user.infrastructure.keycloak.KeycloakProperties;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Component
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakUserRepository implements UserRepository {

    private final KeycloakProperties properties;
    private final Keycloak keycloak;
    private final UserAddressListToJsonListConverter userAddressListConverter;

    private final UsersResource usersResource;
    private static final String ROLE_PREFIX = "ROLE_";

    public KeycloakUserRepository(KeycloakProperties properties, Keycloak keycloak, UserAddressListToJsonListConverter userAddressListConverter) {
        this.properties = properties;
        this.keycloak = keycloak;
        this.usersResource = keycloak.realm(properties.getRealm()).users();
        this.userAddressListConverter = userAddressListConverter;
    }

    @Override
    public Optional<User> findById(UserId id) {
        try {
            UserRepresentation representation = getUserRepresentation(id);
            Map<String, List<String>> attributes = representation.getAttributes();

            List<UserRole> userRoles = getRoleRepresentations(id).stream()
                    .map(RoleRepresentation::getName)
                    .filter(role -> role.startsWith(ROLE_PREFIX))
                    .map(role -> role.substring(ROLE_PREFIX.length()))
                    .map(UserRole::valueOf)
                    .toList();

            List<String> userAddressesJson = attributes.getOrDefault("addresses", Collections.emptyList());
            List<UserAddress> userAddresses = userAddressListConverter.convertToEntityAttribute(userAddressesJson);

            User user = User.builder()
                    .username(representation.getUsername())
                    .userId(UserId.of(UUID.fromString(representation.getId())))
                    .firstName(representation.getFirstName())
                    .lastName(representation.getLastName())
                    .email(representation.getEmail())
                    .phone(attributes.get("phone").getFirst())
                    .createdAt(
                            parseLocalDateTime(attributes.get("createdAt"))
                    )
                    .updatedAt(
                            parseLocalDateTime(attributes.get("updatedAt"))
                    )
                    .deletedAt(
                            parseLocalDateTime(attributes.get("deletedAt"))
                    )
                    .status(UserStatus.valueOf(attributes.get("status").getFirst()))
                    .addresses(userAddresses)
                    .roles(userRoles).build();
            return Optional.of(user);
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public User save(User user) {
        if (user.getUserId() != null) {
            return updateUser(user);
        }
        return saveUser(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        List<UserRepresentation> userRepresentations = usersResource.searchByEmail(email, true);
        return !userRepresentations.isEmpty();
    }

    @Override
    public boolean existsByUsername(String username) {
        List<UserRepresentation> userRepresentations = usersResource.searchByUsername(username, true);
        return !userRepresentations.isEmpty();
    }

    @Override
    public void updatePassword(UserId id, String password) {
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);
        keycloak.realm(properties.getRealm()).users().get(id.getId().toString()).resetPassword(passwordCred);
    }

    @Override
    public void deleteById(UserId userId) {
        UserRepresentation representation = getUserRepresentation(userId);
        Map<String, List<String>> attributes = Objects.requireNonNullElseGet(representation.getAttributes(), HashMap::new);
        attributes.put("status", List.of(UserStatus.WITHDRAW.name()));
        attributes.put("updatedAt", List.of(LocalDateTime.now().toString()));
        attributes.put("deletedAt", List.of(LocalDateTime.now().toString()));
        representation.setAttributes(attributes);
        representation.setEnabled(false);
        keycloak.realm(properties.getRealm()).users().get(userId.getId().toString()).update(representation);
    }

    @Override
    public List<UserAddress> addAddress(UserId userId, UserAddress userAddress) {
        UserRepresentation representation = getUserRepresentation(userId);
        Map<String, List<String>> attributes = Objects.requireNonNullElseGet(representation.getAttributes(), HashMap::new);

        List<String> existingAddressJsons = attributes.getOrDefault("addresses", new ArrayList<>());
        List<String> newAddressJsons = userAddressListConverter.convertToKeycloakAttribute(List.of(userAddress));
        existingAddressJsons.addAll(newAddressJsons);
        attributes.put("addresses", existingAddressJsons);
        representation.setAttributes(attributes);
        keycloak.realm(properties.getRealm()).users().get(userId.getId().toString()).update(representation);

        return findById(userId)
                .map(User::getAddresses)
                .orElseThrow(() -> new IllegalStateException("주소 정보 저장 후 주소 데이터를 찾을 수 없습니다. (내부 오류)"));
    }

    @Override
    public void deleteAddress(UserId userId, UUID targetAddressId) {
        UserRepresentation representation = getUserRepresentation(userId);
        Map<String, List<String>> attributes = Objects.requireNonNullElseGet(representation.getAttributes(), HashMap::new);
        List<String> existingAddressJsons = attributes.get("addresses");
        if (existingAddressJsons == null || existingAddressJsons.isEmpty()) {
            return;
        }
        List<UserAddress> existingAddresses = userAddressListConverter.convertToEntityAttribute(existingAddressJsons);
        List<UserAddress> addressesWithoutTarget = existingAddresses.stream()
                .filter(address -> !address.id().equals(targetAddressId))
                .toList();
        List<String> addressJsonsWithoutTarget = userAddressListConverter.convertToKeycloakAttribute(addressesWithoutTarget);
        attributes.put("addresses", addressJsonsWithoutTarget);
        representation.setAttributes(attributes);
        keycloak.realm(properties.getRealm()).users().get(userId.getId().toString()).update(representation);
    }

    @Override
    public void changeUserRole(UserId userId, UserRole role) {
        RoleScopeResource resource = keycloak.realm(properties.getRealm()).users().get(userId.getId().toString()).roles().realmLevel();
        // 기존 Role 제거
        resource.remove(resource.listAll());
        // 새 Role 추가
        RoleRepresentation representation = keycloak.realm(properties.getRealm()).roles().get(ROLE_PREFIX + role.name()).toRepresentation();
        resource.add(List.of(representation));
    }

    private User saveUser(User user) {
        UserRepresentation userRepresentation = createUserRepresentation(user);
        setUserAttribute(userRepresentation, user);

        Response response = usersResource.create(userRepresentation);

        String userId = CreatedResponseUtil.getCreatedId(response);
        setPassword(userId, user.getPassword());
        setRole(userId, user.getRoles().getFirst());

        return findById(UserId.of(UUID.fromString(userId)))
                .orElseThrow(() -> new IllegalStateException("Keycloak 사용자 생성 후 데이터를 찾을 수 없습니다. (내부 오류)"));
    }

    private User updateUser(User user) {
        UserRepresentation userRepresentation = getUserRepresentation(user.getUserId());
        updateFirstName(userRepresentation, user.getFirstName());
        updateLastName(userRepresentation, user.getLastName());
        updateEmail(userRepresentation, user.getEmail());
        updateAttributes(userRepresentation, user);
        keycloak.realm(properties.getRealm()).users().get(user.getUserId().getId().toString()).update(userRepresentation);
        return findById(user.getUserId())
                .orElseThrow(() -> new IllegalStateException("업데이트 후 사용자를 찾을 수 없습니다. (데이터 무결성 문제 발생)"));
    }

    private LocalDateTime parseLocalDateTime(List<String> localDateTimeStr) {
        return !Objects.isNull(localDateTimeStr) && !localDateTimeStr.isEmpty() ? LocalDateTime.parse(localDateTimeStr.getFirst()) : null;
    }


    private UserRepresentation createUserRepresentation(User user) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);
        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        return userRepresentation;
    }

    private void setUserAttribute(UserRepresentation userRepresentation, User user) {
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("phone", List.of(user.getPhone()));
        attributes.put("createdAt", List.of(user.getCreatedAt().toString()));
        attributes.put("updatedAt", List.of(user.getUpdatedAt().toString()));
        attributes.put("status", List.of(user.getStatus().name()));
        userRepresentation.setAttributes(attributes);
    }

    private void setPassword(String userId, String password) {
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);
        usersResource.get(userId).resetPassword(passwordCred);
    }

    private void setRole(String userId, UserRole role) {
        RoleRepresentation userRole = keycloak.realm(properties.getRealm()).roles().get(ROLE_PREFIX + role.name()).toRepresentation();
        usersResource.get(userId).roles().realmLevel().add(List.of(userRole));
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
            user.setEmail(email);
        }
    }

    private void updateAttributes(UserRepresentation userRepresentation, User user) {
        Map<String, List<String>> attributes = Objects.requireNonNullElseGet(userRepresentation.getAttributes(), HashMap::new);

        if (StringUtils.hasText(user.getPhone())) {
            attributes.put("phone", List.of(user.getPhone()));
        }
        attributes.put("updatedAt", List.of(user.getUpdatedAt().toString()));
        userRepresentation.setAttributes(attributes);
    }

    private UserRepresentation getUserRepresentation(UserId userId) {
        return usersResource.get(userId.getId().toString()).toRepresentation();
    }

    private List<RoleRepresentation> getRoleRepresentations(UserId id) {
        return usersResource.get(id.getId().toString()).roles().realmLevel().listAll();
    }
}
