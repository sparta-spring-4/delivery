package com.zts.delivery.store.domain;

import com.zts.delivery.global.persistence.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Embedded
    private OperationalInfo operationalInfo;

    private String name;
    private String address;
    private String phone;
    private String category;
    private String content;
    @Column(name = "store_picture_url")
    private String picture;

    private Double rating;
    private Integer reviewCount;

    // 영업 상태 여부 (0 = 영업 종료, 1 = 영업 중)
    @Column(name = "is_open")
    private boolean open;

    @Builder
    public Store(Owner owner, String name, String address,
                 String phone, String category, String content,
                 String picture, OperationalInfo operationalInfo) {
        this.owner = owner;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.category = category;
        this.content = content;
        this.picture = picture;
        this.operationalInfo = operationalInfo;

        this.rating = 0.0;
        this.reviewCount = 0;
        this.open = true;
    }

    // 영업 개시
    public void openStore() {
        this.open = true;
    }

    // 영업 종료
    public void closeStore() {
        this.open = false;
    }
}
