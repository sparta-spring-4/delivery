package com.zts.delivery.store.infrastructure.api;

import com.zts.delivery.store.domain.service.StoreAddressService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class KakaoStoreAddressClientTest {
    @Autowired
    StoreAddressService service;

    @Test
    @DisplayName("주소를 위도, 경도로 변환하는 테스트")
    void addressToCoordinateTest() {
        List<Double> coords = service.getCoordinate("서울특별시 강남구");
        System.out.println(coords);
    }
}
