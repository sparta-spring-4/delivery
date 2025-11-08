package com.zts.delivery.store.application.service;

import com.zts.delivery.store.domain.*;
import com.zts.delivery.store.domain.service.StoreAddressService;
import com.zts.delivery.store.presentation.dto.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreUpdateService {

    private final RoleCheck roleCheck;
    private final StoreDetailsRepository detailsRepository;
    private final StoreRepository repository;
    private final StoreAddressService addressService;

    // 매장 일반 정보 수정
    public void updateInfo(UUID storeId, String storeName, String storeTel) {
        Store store = validateAndGet(storeId);

        storeTel = StringUtils.hasText(storeTel) ? storeTel.replaceAll("\\D", "") : storeTel;

        store.changeInfo(storeName, storeTel);

        repository.save(store);
    }

    // 매장 운영 정보 수정
    public void updateOperatingInfo(UUID storeId, LocalTime startTime, LocalTime endTime, List<DayOfWeek> weekdays) {
        Store store = validateAndGet(storeId);
        store.changeOperatingInfo(startTime, endTime, weekdays);
    }

    // 매장 주소 정보 수정
    public void updateAddress(UUID storeId, String address) {
        Store store = validateAndGet(storeId);
        store.changeAddress(address, addressService);
    }

    // 매장 리뷰 수정
    public void updateReview(UUID storeId, int reviewCount, BigDecimal averageReviewScore) {
        Store store = validateAndGet(storeId);
        store.changeReview(reviewCount, averageReviewScore);
    }

    // 매장 존재 여부 및 수정 권한 검증 후 엔티티 반환
    private Store validateAndGet(UUID id) {
        StoreId storeId = StoreId.of(id);
        Store.exists(storeId, repository); // 매장 등록 여부 확인

        Store store = detailsRepository.findById(storeId);

        store.isEditable(roleCheck); // 매장 수정 권한 여부 확인

        return store;
    }

    // 매장 분류 추가
    public void addCategory(UUID storeId, Category category, boolean active) {
        Store store = validateAndGet(storeId);
        store.addCategory(category, active);
    }

    // 매장 분류 변경
    public void changeCategories(UUID storeId, List<CategoryDto> categories) {
        Store store = validateAndGet(storeId);
        store.truncateCategory();
        categories.forEach(c -> store.addCategory(c.category(), c.active()));
    }

    // 매장 분류 삭제
    public void removeCategory(UUID storeId, Category category) {
        Store store = validateAndGet(storeId);
        store.removeCategory(category);
    }

}
