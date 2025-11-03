package com.zts.delivery.order.domain.cart;

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
public class CartItem {
    @Embedded
    @Column(name="item_id")
    private ItemId id;

    @Column(length=60)
    private String itemName;

    @Convert(converter = PriceConverter.class)
    private Price itemPrice;

    @Builder
    public CartItem(ItemId id, String itemName,Price itemPrice) {
        this.id = id;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }
}
