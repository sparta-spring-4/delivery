package com.zts.delivery.store.infrastructure.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zts.delivery.store.domain.service.StoreAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoStoreAddressClient implements StoreAddressService {
    @Value("${kakao.restapi.key}")
    private String apiKey;

    private final ObjectMapper om;

    @Override
    public List<Double> getCoordinate(String address) {
        RestClient client = RestClient
                .builder()
                .baseUrl("https://dapi.kakao.com/v2/local/search/address.json?query=" + address).build();
        ResponseEntity<String> res = client.get()
                .header("Authorization", "KakaoAK " + apiKey)
                .retrieve()
                .toEntity(String.class);
        if (res.getStatusCode().is2xxSuccessful()) {
            try {
                String body = res.getBody();
                JsonNode node = om.readTree(body);
                JsonNode docs = node.get("documents");
                if (!docs.isEmpty()) {
                    JsonNode addr = docs.get(0).get("address");
                    addr = addr.toString().equals("null") ? docs.get(0).get("road_address") : addr;
                    double latitude = addr.get("y").asDouble(0.0);  // 위도
                    double longitude = addr.get("x").asDouble(0.0); // 경도
                    return List.of(latitude, longitude);
                }

            } catch (JsonProcessingException e) {
                log.error("주소 좌표 변환 에러", e);
            }
        }

        return List.of(0.0, 0.0);
    }
}
