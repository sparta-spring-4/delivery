package com.zts.delivery.order.domain.cart;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.global.persistence.converter.IntegerListConverter;
import com.zts.delivery.global.persistence.converter.PriceConverter;
import com.zts.delivery.menu.domain.ItemId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import java.util.ArrayList;
import java.util.Collections;
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

    @Column(length = 60)
    private String itemName;

    @Column(nullable = false)
    private int quantity;

    @Convert(converter = IntegerListConverter.class)
    @Column(name = "selected_options")
    private List<Integer> selectedOptions = new ArrayList<>();

    @Convert(converter = PriceConverter.class)
    private Price price;

    @Builder
    public CartItem(ItemId id, String itemName, int quantity, List<Integer> selectedOptions, Price price) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
        this.selectedOptions = (selectedOptions != null) ? new ArrayList<>(selectedOptions) : new ArrayList<>();
        this.price = price;
    }

    public CartItem updateQuantity(int newQuantity) {
        return new CartItem(
            this.id,
            this.itemName,
            newQuantity,
            this.selectedOptions,
            this.price
        );
    }

    public CartItem updateOptions(List<Integer> newIndices) {
        return new CartItem(
            this.id,
            this.itemName,
            this.quantity,
            newIndices,
            this.price
        );
    }

    public boolean matches(ItemId itemId, List<Integer> options) {
        // 1. ID가 다르면 무조건 false
        if (!this.id.equals(itemId)) {
            return false;
        }

        // 2. 비교 대상 옵션 목록도 정렬하여 동일한 기준으로 비교
        List<Integer> safeOptions = (options != null) ? new ArrayList<>(options) : new ArrayList<>();
        Collections.sort(safeOptions);

        // 3. 정렬된 옵션 목록 비교
        return this.selectedOptions.equals(safeOptions);
    }

    public Price calculateItemPrice() {
        return this.price.multiply(this.quantity);
    }
}