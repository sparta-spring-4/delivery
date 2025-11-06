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
    private Price optionsPrice;

    @Builder
    public CartItem(ItemId id, String itemName, int quantity, List<Integer> selectedOptions, Price price, Price optionsPrice) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
        this.selectedOptions = (selectedOptions != null) ? new ArrayList<>(selectedOptions) : new ArrayList<>();
        this.price = price;
        this.optionsPrice = optionsPrice;
    }

    public static CartItem create(Item item, List<Integer> selectedOptionIndices, int quantity) {

        Price optionsPrice = item.getOptionsPrice(selectedOptionIndices);

        return CartItem.builder()
            .id(item.getId())
            .itemName(item.getName())
            .quantity(quantity)
            .selectedOptions(selectedOptionIndices)
            .price(item.getPrice())
            .optionsPrice(optionsPrice)
            .build();
    }

    public CartItem updateQuantity(boolean isAdding) {
        int newQuantity = this.quantity + (isAdding ? 1 : -1);

        return CartItem.builder()
            .id(this.id)
            .itemName(this.itemName)
            .quantity(newQuantity)
            .selectedOptions(this.selectedOptions)
            .price(this.price)
            .optionsPrice(this.optionsPrice)
            .build();
    }

    public CartItem chooseOptions(Item item, List<Integer> newIndices) {
        Price newOptionsPrice = item.getOptionsPrice(newIndices);

        return CartItem.builder()
            .id(this.id)
            .itemName(this.itemName)
            .quantity(this.quantity)
            .selectedOptions(newIndices)
            .price(this.price)
            .optionsPrice(newOptionsPrice)
            .build();
    }

    public Price calculateTotalPrice() {
        Price totalUnitPrice = calculateTotalUnitPrice();
        return totalUnitPrice.multiply(this.quantity);
    }

    public Price calculateTotalUnitPrice() {
        return this.price.add(this.optionsPrice);
    }
}