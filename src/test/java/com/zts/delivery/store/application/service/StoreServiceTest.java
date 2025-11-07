package com.zts.delivery.store.application.service;

import com.zts.delivery.store.domain.Category;
import com.zts.delivery.store.domain.Store;
import com.zts.delivery.store.domain.StoreId;
import com.zts.delivery.store.domain.StoreRepository;
import com.zts.delivery.store.presentation.dto.CategoryDto;
import com.zts.delivery.store.presentation.dto.StoreRequest;
import com.zts.delivery.user.test.MockUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

import static java.time.DayOfWeek.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ActiveProfiles("test")
public class StoreServiceTest {
    @Autowired
    StoreRepository repository;

    @Autowired
    StoreCreateService createService;

    StoreRequest request;

    @BeforeEach
    void init() {
        request = StoreRequest.builder()
                .storeName("테스트 매장")
                .storeAddress("서울특별시 강남구")
                .storeTel("02-100-1000")
                .startHour(LocalTime.of(10, 0))
                .endHour(LocalTime.of(19, 0))
                .weekdays(List.of(MONDAY,TUESDAY, WEDNESDAY))
                .category(List.of(new CategoryDto(Category.KOREAN, true), new CategoryDto(Category.CHINESE, true)))
                .build();
    }

    @Test
    @Transactional
    @DisplayName("상점 등록 테스트")
    @MockUser(roles = "OWNER")
    void createStoreTest() {

        assertDoesNotThrow(() -> {
            StoreId storeId = createService.create(request);

            Store store = repository.findById(storeId).orElseThrow();
            System.out.println(store);
        });
    }
}
