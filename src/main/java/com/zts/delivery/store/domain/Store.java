package com.zts.delivery.store.domain;

import com.zts.delivery.global.persistence.common.BaseEntity;
import com.zts.delivery.user.domain.UserId;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@ToString
@Getter
@Entity
@Table(name = "P_STORE")
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity {

    @EmbeddedId
    private StoreId id;

    @Embedded
    private Owner owner;

    @Column(length = 100, nullable = false)
    private String storeName;

    @Column(length = 45, nullable = false)
    private String storeTel;

    @Embedded
    private StoreAddress address;

    @Embedded
    private OperatingInfo operatingInfo;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "P_STORE_CATEGORY", joinColumns = @JoinColumn(name = "store_id"))
    @OrderColumn(name = "category_idx")
    private List<StoreCategory> categories;

    @Builder
    public Store(StoreId id, String storeName, String storeTel, LocalTime startHour, LocalTime endHour, List<DayOfWeek> weekdays,
                 UserId userId, String userName, StoreAddress address, List<StoreCategory> categories) {

        this.id = Objects.requireNonNullElse(id, StoreId.of());
        this.owner = new Owner(userId, userName);
        this.storeName = storeName;
        this.storeTel = storeTel;
        this.operatingInfo = new OperatingInfo(startHour, endHour, weekdays);
        this.address = address;
        setCategories(categories);
    }

    private void setCategories(List<StoreCategory> categories) {
        if (categories == null || categories.isEmpty()) return;

        this.categories = categories.stream().distinct().toList();
    }
}
