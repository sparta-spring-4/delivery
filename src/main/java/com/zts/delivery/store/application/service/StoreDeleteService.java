package com.zts.delivery.store.application.service;

import com.zts.delivery.store.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreDeleteService {

    private final RoleCheck roleCheck;
    private final StoreRepository storeRepository;
    private final StoreDetailsRepository detailsRepository;
    private final StoreUser storeUser;

    @Transactional
    public void delete(UUID storeId) {
        StoreId id = StoreId.of(storeId);
        Store.exists(id, storeRepository); // 매장 존재 여부 확인
        Store store = detailsRepository.findById(id);

        store.isEditable(roleCheck); // 삭제 권한 여부 확인
        store.markAsDeleted(storeUser.getUserName());

        storeRepository.save(store);
    }
}
