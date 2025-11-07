package com.zts.delivery.user.domain.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zts.delivery.user.domain.UserAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Repository(keycloak)에 저장할 때 List<json> 형태로 저장한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserAddressListToJsonListConverter {

    private final ObjectMapper objectMapper;

    public List<String> convertToKeycloakAttribute(List<UserAddress> attribute) {
        return attribute.stream().map(userAddress -> {
            try {
                return objectMapper.writeValueAsString(userAddress);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize address object to JSON.", e);
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    public List<UserAddress> convertToEntityAttribute(List<String> keycloakData) {
        return keycloakData.stream().map(json -> {
            try {
                return objectMapper.readValue(json, UserAddress.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize JSON to Address object.", e);
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }
}


