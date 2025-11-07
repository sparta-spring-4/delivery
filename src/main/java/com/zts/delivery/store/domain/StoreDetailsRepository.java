package com.zts.delivery.store.domain;

import com.zts.delivery.global.persistence.common.ListData;
import com.zts.delivery.store.domain.dto.SearchDto;
import com.zts.delivery.store.infrastructure.persistence.dto.StoreDto;

import java.util.List;

// 조회 전용 Repository
public interface StoreDetailsRepository {
    Store findById(StoreId storeId);
    List<Category> findAllCategories(StoreId storeId);
    ListData<StoreDto> findAll(SearchDto search, int page, int size);
}
