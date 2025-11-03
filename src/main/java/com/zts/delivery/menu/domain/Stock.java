package com.zts.delivery.menu.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {
    private int value;

    public Stock(int value) {
        this.value = Math.max(0, value);
    }

    public Stock add(int add) {
        return new Stock(value + add);
    }

    public Stock minus(int minus) {
        return new Stock(value - minus);
    }

}
