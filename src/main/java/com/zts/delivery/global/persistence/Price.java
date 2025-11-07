package com.zts.delivery.global.persistence;

import jakarta.persistence.Embeddable;
import lombok.*;

@ToString
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Price {
    private int value;

    public Price(int value) {
        this.value = value;
    }

    public Price add(Price price) {
        return new Price(this.value + price.value);
    }

    public Price multiply(int multiplier) {
        return new Price(this.value * multiplier);
    }
}
