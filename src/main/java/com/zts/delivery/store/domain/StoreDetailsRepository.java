package com.zts.delivery.store.domain;

// 조회 전용 Repository
public interface StoreDetailsRepository {
    Store findById(StoreId storeId);
}
