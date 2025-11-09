package com.zts.delivery.store.presentation.controller;

import com.zts.delivery.global.persistence.common.ListData;
import com.zts.delivery.store.application.service.StoreCreateService;
import com.zts.delivery.store.application.service.StoreDeleteService;
import com.zts.delivery.store.application.service.StoreUpdateService;
import com.zts.delivery.store.domain.Category;
import com.zts.delivery.store.domain.StoreDetailsRepository;
import com.zts.delivery.store.domain.StoreId;
import com.zts.delivery.store.domain.dto.SearchDto;
import com.zts.delivery.store.infrastructure.persistence.dto.StoreDto;
import com.zts.delivery.store.presentation.dto.CategoryDto;
import com.zts.delivery.store.presentation.dto.SearchRequest;
import com.zts.delivery.store.presentation.dto.StoreRequest;
import com.zts.delivery.store.presentation.dto.StoreResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Tag(name = "매장 API", description = "매장 추가 / 정보 수정 / 삭제 / 카테고리 관리 / 검색 기능을 제공합니다.")
@RestController
@RequestMapping("v1/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreCreateService createService;
    private final StoreUpdateService updateService;
    private final StoreDeleteService deleteService;
    private final StoreDetailsRepository detailsRepository;

    @Operation(summary = "매장 추가", description = "매장을 추가합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    public StoreResponse createStore(@Valid @RequestBody StoreRequest request) {
        StoreId storeId = createService.create(request);

        return new StoreResponse(storeId);
    }

    @Operation(summary = "매장 정보 수정", description = "매장 정보를 수정합니다.")
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

    @Operation(summary = "매장 삭제", description = "매장을 삭제합니다.")
    @DeleteMapping("/{storeId}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStore(@PathVariable("storeId") UUID storeId) {
        deleteService.delete(storeId);
    }

    @Operation(summary = "매장 분류 추가", description = "매장 분류를 추가합니다.")
    @PostMapping("/{storeId}/category")
    @ResponseStatus(HttpStatus.CREATED)
    public void addCategory(@PathVariable("storeId") UUID storeId, @RequestBody CategoryDto categoryDto) {
        updateService.addCategory(storeId, categoryDto.category(), categoryDto.active());
    }

    @Operation(summary = "매장 분류 수정", description = "매장 분류를 수정(치환)합니다.")
    @PutMapping("/{storeId}/categories")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeCategories(@PathVariable("storeId") UUID storeId, @RequestBody List<CategoryDto> categories) {
        updateService.changeCategories(storeId, categories);
    }

    @Operation(summary = "매장 분류 삭제", description = "매장 분류를 삭제합니다.")
    @PatchMapping("/{storeId}/category")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable UUID storeId, @RequestBody List<Category> categories) {

        categories.forEach(c -> updateService.removeCategory(storeId, c));
    }

    @Operation(summary = "매장 검색", description = "매장을 검색합니다.")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'MANAGER', 'MASTER')")
    public ListData<StoreDto> searchStore(SearchRequest request) { // page, size
        SearchDto search = SearchDto.builder()
                .storeName(request.storeName())
                .keyword(request.keyword())
                .storeTel(request.storeTel())
                .sido(request.sido())
                .sigugun(request.sigugun())
                .dong(request.dong())
                .category(request.category())
                .build();

        return detailsRepository.findAll(search, Objects.requireNonNullElse(request.page(), 1), Objects.requireNonNullElse(request.size(), 20));
    }

}
