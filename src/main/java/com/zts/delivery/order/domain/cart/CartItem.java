package com.zts.delivery.order.domain.cart;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.global.persistence.converter.IntegerListConverter;
import com.zts.delivery.global.persistence.converter.PriceConverter;
import com.zts.delivery.infrastructure.execption.ApplicationException;
import com.zts.delivery.menu.domain.Item;
import com.zts.delivery.menu.domain.ItemId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@EqualsAndHashCode
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "item_id", nullable = false))
    private ItemId id;

    @Column(nullable = false)
    private int quantity;

    @Convert(converter = IntegerListConverter.class)
    @Column(name = "selected_options")
    private List<Integer> selectedOptions = new ArrayList<>();

    @Convert(converter = PriceConverter.class)
    private Price price;

    @Builder
    public CartItem(ItemId id, int quantity, List<Integer> selectedOptions, Price price) {
        this.id = id;
        this.quantity = quantity;
        this.selectedOptions = (selectedOptions != null) ? new ArrayList<>(selectedOptions) : new ArrayList<>();
        this.price = price;
    }

    public CartItem updateQuantity(boolean isAdding) {
        if (quantity == 1 && !isAdding) {

        }
        if (isAdding) {
            quantity ++;
        }
        else {
            quantity --;
        }

        return CartItem.builder()
            .id(this.id)
            .quantity(this.quantity)
            .selectedOptions(this.selectedOptions)
            .price(calculateItemPrice())
            .build();
    }

    public CartItem chooseOptions(Item item, List<Integer> newIndices) {
        CartItem updatedItem = CartItem.builder()
            .id(this.id)
            .quantity(this.quantity)
            .selectedOptions(newIndices)
            .price(price.add(item.getOptionsPrice(newIndices)))
            .build();
        updatedItem.calculateItemPrice();
        return updatedItem;
    }

    public Price calculateItemPrice() {
        return this.price.multiply(this.quantity);
    }
}