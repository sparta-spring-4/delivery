package com.zts.delivery.store.domain;

import org.springframework.data.jpa.repository.JpaRepository;

// 도메인 로직 처리를 위한 Repository
public interface StoreRepository extends JpaRepository<Store, StoreId> {
}
