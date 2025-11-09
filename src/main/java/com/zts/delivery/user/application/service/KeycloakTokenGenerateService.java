package com.zts.delivery.user.application.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.zts.delivery.global.infrastructure.execption.ApplicationException;
import com.zts.delivery.global.infrastructure.execption.ErrorCode;
import com.zts.delivery.user.application.dto.KeycloakErrorResponse;
import com.zts.delivery.user.infrastructure.keycloak.KeycloakProperties;
import com.zts.delivery.user.application.dto.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakTokenGenerateService implements TokenGenerateService {

    private final KeycloakProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public TokenInfo generate(String username, String password) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", properties.getClientId());
        form.add("client_secret", properties.getClientSecret());
        form.add("username", username);
        form.add("password", password);
        form.add("scope", "openid profile email");
        RestClient client = RestClient.create();
        ResponseEntity<TokenInfo> res = client.post()
                .uri(String.format("%s/realms/%s/protocol/openid-connect/token", properties.getServerUrl(), properties.getRealm()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    KeycloakErrorResponse keycloakErrorResponse = objectMapper.readValue(response.getBody(), KeycloakErrorResponse.class);
                    throw new ApplicationException(response.getStatusCode(), "TOKEN_ISSUE_ERROR", keycloakErrorResponse.errorDescription());
                })
                .toEntity(TokenInfo.class);
        if (res.getStatusCode().is2xxSuccessful()) {
            return res.getBody();
        }
        throw new ApplicationException(ErrorCode.UNAUTHORIZED);
    }
}