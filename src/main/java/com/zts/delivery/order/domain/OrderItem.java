package com.zts.delivery.order.domain;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.global.persistence.converter.PriceConverter;
import com.zts.delivery.menu.domain.ItemId;
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
public class OrderItem {
    @Embedded
    private ItemId itemId;

    @Column(nullable = false, length=60)
    private String itemName;

    @Convert(converter = PriceConverter.class)
    private Price price;

    private int quantity;

    @Convert(converter = PriceConverter.class)
    private Price totalPrice;

    @Builder
    public OrderItem(ItemId itemId, String itemName ,Price price, int quantity) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = calculateTotalPrice();
    }

    private Price calculateTotalPrice() {
        return price.multiply(quantity);
    }
}
