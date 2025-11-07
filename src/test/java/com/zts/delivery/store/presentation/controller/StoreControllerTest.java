package com.zts.delivery.store.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zts.delivery.store.application.service.StoreCreateService;
import com.zts.delivery.store.domain.Category;
import com.zts.delivery.store.domain.Store;
import com.zts.delivery.store.domain.StoreId;
import com.zts.delivery.store.domain.StoreRepository;
import com.zts.delivery.store.presentation.dto.CategoryDto;
import com.zts.delivery.store.presentation.dto.StoreRequest;
import com.zts.delivery.store.presentation.dto.StoreResponse;
import com.zts.delivery.user.test.MockUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.List;

import static java.time.DayOfWeek.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class StoreControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper om;

    StoreRequest request;

    @Autowired
    StoreRepository repository;

    @Autowired
    StoreCreateService createService;

    @BeforeEach
    void setup() {
        request = StoreRequest.builder()
                .storeName("테스트 매장")
                .storeAddress("서울특별시 강남구")
                .storeTel("02-1000-1000")
                .startHour(LocalTime.of(9, 0))
                .endHour(LocalTime.of(18, 0))
                .weekdays(List.of(MONDAY, TUESDAY, WEDNESDAY))
                .category(List.of(new CategoryDto(Category.KOREAN, true), new CategoryDto(Category.CHINESE, true), new CategoryDto(Category.CHICKEN, true)))
                .build();

        // {"storeName": "테스트 매장", "storeAddress": "테스트 주소", ...}
    }

    @Test
    @DisplayName("매장 등록 테스트")
    @MockUser(roles = "OWNER")
    void createStoreTest() throws Exception {
        String body = om.writeValueAsString(request); // 자바 객체 -> JSON 문자열
        String res = mockMvc.perform(post("/v1/stores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        StoreResponse response  = om.readValue(res, StoreResponse.class);
        StoreId id = response.storeId();
        Store store = repository.findById(id).orElse(null);
        System.out.println(store);
    }

    @Test
    @DisplayName("매장 수정 테스트")
    @MockUser(roles = "OWNER")
    void updateStoreTest() throws Exception {
        StoreId storeId = createService.create(request);

        StoreRequest req = StoreRequest.builder()
                .storeName("(수정)테스트 매장")
                .storeAddress("(수정)서울특별시 강남구")
                .storeTel("(수정)02-1000-1000")
                .startHour(LocalTime.of(12, 0))
                .endHour(LocalTime.of(23, 0))
                .weekdays(List.of(MONDAY, WEDNESDAY))
                .build();

        String body = om.writeValueAsString(req);

        mockMvc.perform(patch("/v1/stores/" + storeId.getId().toString())
            .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print());

        Store store = repository.findById(storeId).orElse(null);
        System.out.println(store);

    }

    @Test
    @DisplayName("매장 삭제 테스트")
    @MockUser(roles = "OWNER")
    void deleteStoreTest() throws Exception {
        StoreId storeId = createService.create(request);

        mockMvc.perform(delete("/v1/stores/" + storeId.getId().toString()))
                .andDo(print());

        Store store = repository.findById(storeId).orElse(null);
        System.out.println("result:" + store.getDeletedAt() + "," + store.getDeletedBy());

    }

    @Test
    @DisplayName("매장 분류 추가")
    @MockUser(roles = "OWNER")
    void addCategoryTest() throws Exception {
        StoreId storeId = createService.create(request);
        CategoryDto categoryDto = new CategoryDto(Category.CHICKEN, true);
        String body = om.writeValueAsString(categoryDto);

        mockMvc.perform(post("/v1/stores/" + storeId.getId().toString() + "/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print());

        Store store = repository.findById(storeId).orElse(null);
        System.out.println(store);
    }

    @Test
    @DisplayName("매장 분류 변경")
    @MockUser(roles = "OWNER")
    void changeCategoriesTest() throws Exception {
        StoreId storeId = createService.create(request);
        List<CategoryDto> categories = List.of(new CategoryDto(Category.PIZZA, true), new CategoryDto(Category.CHICKEN, true));
        String body = om.writeValueAsString(categories);

        mockMvc.perform(put("/v1/stores/" + storeId.getId().toString() + "/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print());

        Store store = repository.findById(storeId).orElse(null);
        System.out.println(store);
    }

    @Test
    @DisplayName("매장 분류 삭제")
    @MockUser(roles = "OWNER")
    void deleteCategoryTest() throws Exception {
        StoreId storeId = createService.create(request);
        List<Category> categories = List.of(Category.CHICKEN, Category.KOREAN);

        String body = om.writeValueAsString(categories);

        mockMvc.perform(patch("/v1/stores/" + storeId.getId().toString() + "/category")
        .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print());

        Store store = repository.findById(storeId).orElse(null);
        System.out.println(store);

    }
}
