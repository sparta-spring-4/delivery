package com.zts.delivery.store.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@ToString
@Getter
@Embeddable
@EqualsAndHashCode(of = "category") // 동일한 카테고리는 active 여부와 관계없이 같은 값으로 판단
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreCategory {
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private Category category;

    private boolean active; // true: 카테고리 활성화, false: 비활성화
}
