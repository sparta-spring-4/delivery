package com.zts.delivery.order.domain;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.global.persistence.converter.PriceConverter;
import com.zts.delivery.menu.domain.ItemId;
import com.zts.delivery.order.infrastructure.OrderItemOptionConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import java.util.ArrayList;
import java.util.List;
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

    @Column(nullable = false, length = 60)
    private String itemName;

    private int quantity;

    @Column(length = 500)
    @Convert(converter = OrderItemOptionConverter.class)
    private List<OrderItemOption> options;

    @Convert(converter = PriceConverter.class)
    private Price price;


    @Builder
    public OrderItem(ItemId itemId, String itemName, int quantity, List<OrderItemOption> options, Price price) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.options = (options != null) ? new ArrayList<>(options) : new ArrayList<>();
        this.price = price;
    }
}
