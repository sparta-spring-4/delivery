package com.zts.delivery.store.presentation.controller;

import com.zts.delivery.store.application.service.StoreCreateService;
import com.zts.delivery.store.application.service.StoreDeleteService;
import com.zts.delivery.store.application.service.StoreUpdateService;
import com.zts.delivery.store.domain.Category;
import com.zts.delivery.store.domain.StoreId;
import com.zts.delivery.store.presentation.dto.CategoryDto;
import com.zts.delivery.store.presentation.dto.StoreRequest;
import com.zts.delivery.store.presentation.dto.StoreResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreCreateService createService;
    private final StoreUpdateService updateService;
    private final StoreDeleteService deleteService;

    /**
     * 매장 추가
     * @param request
     * @return
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    public StoreResponse createStore(@Valid @RequestBody StoreRequest request) {
        StoreId storeId = createService.create(request);

        return new StoreResponse(storeId);
    }

    /**
     * 매장 정보 수정
     * @param storeId
     * @param request
     */
    @PatchMapping("/{storeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    public void updateStore(@PathVariable("storeId") UUID storeId, @Valid @RequestBody StoreRequest request) {
        // 매장 일반 정보
        updateService.updateInfo(storeId, request.storeName(), request.storeTel());

        // 매장 운영 정보
        updateService.updateOperatingInfo(storeId, request.startHour(), request.endHour(), request.weekdays());

        // 매장 주소 변경
        updateService.updateAddress(storeId, request.storeAddress());
    }

    /**
     * 매장 삭제
     * @param storeId
     */
    @DeleteMapping("/{storeId}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStore(@PathVariable("storeId") UUID storeId) {
        deleteService.delete(storeId);
    }

    /**
     * 매장 분류 추가
     * @param storeId
     * @param categoryDto
     */
    @PostMapping("/{storeId}/category")
    @ResponseStatus(HttpStatus.CREATED)
    public void addCategory(@PathVariable("storeId") UUID storeId, @RequestBody CategoryDto categoryDto) {
        updateService.addCategory(storeId, categoryDto.category(), categoryDto.active());
    }

    /**
     * 매장 분류 치환(수정)
     * @param storeId
     * @param categories
     */
    @PutMapping("/{storeId}/categories")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeCategories(@PathVariable("storeId") UUID storeId, @RequestBody List<CategoryDto> categories) {
        updateService.changeCategories(storeId, categories);
    }

    /**
     * 매장 분류 삭제
     * @param storeId
     * @param categories
     */
    @PatchMapping("/{storeId}/category")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable UUID storeId, @RequestBody List<Category> categories) {

        categories.forEach(c -> updateService.removeCategory(storeId, c));
    }

}
