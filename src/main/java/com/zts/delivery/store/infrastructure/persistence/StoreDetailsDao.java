package com.zts.delivery.store.infrastructure.persistence;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zts.delivery.store.domain.QStore;
import com.zts.delivery.store.domain.Store;
import com.zts.delivery.store.domain.StoreDetailsRepository;
import com.zts.delivery.store.domain.StoreId;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoreDetailsDao implements StoreDetailsRepository {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Override
    public Store findById(StoreId storeId) {
        QStore store = QStore.store;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(store.id.eq(storeId))
                .and(store.deletedAt.isNull()); // 삭제가 되지 않은 매장

        return queryFactory.selectFrom(store)
                .where(builder)
                .fetchFirst();
    }

}
