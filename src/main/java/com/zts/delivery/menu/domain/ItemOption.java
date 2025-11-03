package com.zts.delivery.menu.domain;

import com.zts.delivery.global.persistence.Price;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemOption {

    @Column(nullable = false, length=60)
    private String optionName;

    @Embedded
    private Price price;

    @Builder
    public ItemOption(String optionName, Price price) {
        this.optionName = optionName;
        this.price = price;
    }
}