package com.zts.delivery.store.infrastructure.persistence;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zts.delivery.global.persistence.common.ListData;
import com.zts.delivery.global.persistence.common.Pagination;
import com.zts.delivery.store.domain.*;
import com.zts.delivery.store.domain.dto.SearchDto;
import com.zts.delivery.store.infrastructure.persistence.dto.StoreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

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

    public List<Category> findAllCategories(StoreId storeId) {
        QStore store = QStore.store;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(store.id.eq(storeId))
                .and(store.categories.any().active.eq(true))
                .and(store.deletedAt.isNull());

        List<StoreCategory>  categories = queryFactory.select(store.categories).from(store)
                .where(builder)
                .fetchFirst();

        return categories == null ? null : categories.stream()
                .map(StoreCategory::getCategory)
                .toList();
    }

    public ListData<StoreDto> findAll(SearchDto search, int page, int size) {
        QStore store = QStore.store;
        BooleanBuilder andBuilder = new BooleanBuilder();

        // 검색 조건 처리 Start
        // 매장명
        String storeName = search.storeName();
        if (StringUtils.hasText(storeName)) {
            andBuilder.and(store.storeName.contains(storeName.trim()));
        }

        // 전화번호
        String storeTel = search.storeTel();// 010-1000-1000, 010 1000 1000, 010/1000/1000
        if (StringUtils.hasText(storeTel)) {
            storeTel = storeTel.replaceAll("\\D", "");
            andBuilder.and(store.storeTel.contains(storeTel.trim()));
        }

        // 주소 시도 -> 시구군 -> 동
        String sido = search.sido();
        String sigugun = search.sigugun();
        String dong = search.dong();
        StringPath address = store.address.address;
        if (StringUtils.hasText(sido)) {
            andBuilder.and(address.contains(sido.trim()));

            if (StringUtils.hasText(sigugun)) {
                andBuilder.and(address.contains(sigugun.trim()));

                if (StringUtils.hasText(dong)) {
                    andBuilder.and(address.contains(dong.trim()));
                }
            }
        }

        // 분류 조회
        List<Category> categories = search.category();
        if (categories != null && !categories.isEmpty()) {
            andBuilder.and(store.categories.any().category.in(categories));
        }

        // 통합 검색 - 키워드 검색 : storeName + storeTel + address
        String keyword = search.keyword();
        if (StringUtils.hasText(keyword)) {
            andBuilder.and(store.storeName.concat(store.storeTel).concat(address).contains(keyword.trim()));
        }

        // 검색 조건 처리 End
        page = Math.max(page, 1);
        size = size < 1 ? 20 : size;
        int offset = (page - 1) * size;
        List<StoreDto> items = queryFactory.selectFrom(store)
                .leftJoin(store.categories)
                .fetchJoin()
                .where(andBuilder)
                .offset(offset)
                .limit(size)
                .orderBy(store.createdAt.desc())
                .fetch()
                .stream()
                .map(item -> StoreDto.builder()
                        .ownerId(item.getOwner().getId().getId())
                        .ownerName(item.getOwner().getName())
                        .storeName(item.getStoreName())
                        .storeTel(item.getStoreTel())
                        .address(item.getAddress().getAddress())
                        .latitude(item.getAddress().getLatitude())
                        .longitude(item.getAddress().getLongitude())
                        .startHour(item.getOperatingInfo().getStartHour())
                        .endHour(item.getOperatingInfo().getEndHour())
                        .weekdays(item.getOperatingInfo().getWeekdays())
                        .category(item.getCategories().stream().map(StoreCategory::getCategory).toList())
                        .createdAt(item.getCreatedAt())
                        .reviewCount(item.getReviewCount())
                        .averageReviewScore(item.getAverageReviewScore())
                        .build())
                .toList();

        long total = Objects.requireNonNullElse(queryFactory.select(store.count()).from(store)
                .where(andBuilder)
                .fetchFirst(), 0L);


        int totalPages = (int)Math.ceil(total / (double)size); // 전체 페이지 갯수

        Pagination pagination = Pagination.builder()
                .page(page)
                .size(size)
                .total((int)total)
                .totalPages(totalPages)
                .build();

        return new ListData<>(items, pagination);
    }

}
