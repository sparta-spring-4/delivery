package com.zts.delivery.menu.domain;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.global.persistence.converter.PriceConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
    private String name;

    @Convert(converter = PriceConverter.class)
    private Price price;

    @Builder
    public ItemOption(String name, Price price) {
        this.name = name;
        this.price = price;
    }
}