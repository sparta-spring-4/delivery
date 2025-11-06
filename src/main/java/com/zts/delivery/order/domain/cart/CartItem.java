package com.zts.delivery.order.domain.cart;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.global.persistence.converter.IntegerListConverter;
import com.zts.delivery.global.persistence.converter.PriceConverter;
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

    @Column(nullable = false, length = 60)
    private String itemName;

    @Column(nullable = false)
    private int quantity;

    @Convert(converter = IntegerListConverter.class)
    @Column(name = "selected_options")
    private List<Integer> selectedOptions = new ArrayList<>();

    @Convert(converter = PriceConverter.class)
    private Price price;

    @Convert(converter = PriceConverter.class)
    private Price totalPrice;

    @Builder
    public CartItem(ItemId id, String itemName, int quantity, List<Integer> selectedOptions, Price price, Price totalPrice) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
        this.selectedOptions = (selectedOptions != null) ? new ArrayList<>(selectedOptions) : new ArrayList<>();
        this.price = price;
        this.totalPrice = totalPrice;
    }

    public static CartItem create(Item item, List<Integer> selectedOptions) {
        Price price = item.getPrice();
        Price optionsPrice = item.getOptionsPrice(selectedOptions);

        return CartItem.builder()
            .id(item.getId())
            .itemName(item.getName())
            .quantity(1)
            .selectedOptions(selectedOptions)
            .price(price)
            .totalPrice(price.add(optionsPrice))
            .build();
    }

    public void updateQuantity(boolean isAdding) {
        int newQuantity = this.quantity + (isAdding ? 1 : -1);

        CartItem.builder()
                .id(this.id)
                .itemName(this.itemName)
                .quantity(newQuantity)
                .selectedOptions(this.selectedOptions)
                .price(this.price)
                .totalPrice(totalPrice.multiply(newQuantity))
                .build();
    }

    public void updateOptions(Item item, List<Integer> newIndices) {
        Price newOptionsPrice = item.getOptionsPrice(newIndices);

        CartItem.builder()
            .id(this.id)
            .itemName(this.itemName)
            .quantity(this.quantity)
            .selectedOptions(newIndices)
            .price(this.price)
            .totalPrice(this.price.add(newOptionsPrice).multiply(this.quantity))
            .build();
    }
}