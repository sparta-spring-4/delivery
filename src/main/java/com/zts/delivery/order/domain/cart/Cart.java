package com.zts.delivery.order.domain.cart;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.global.persistence.common.DateAudit;
import com.zts.delivery.global.persistence.converter.PriceConverter;
import com.zts.delivery.menu.domain.Item;
import com.zts.delivery.menu.domain.ItemId;
import com.zts.delivery.user.UserId;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@ToString
@Getter
@Entity
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Cart extends DateAudit {

    @EmbeddedId
    private CartId id;

    @Embedded
    private UserId userId;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "P_CART_ITEM", joinColumns = @JoinColumn(name = "cart_id"))
    @OrderColumn(name = "item_idx")
    private List<CartItem> cartItems;

    @Convert(converter = PriceConverter.class)
    private Price totalPrice;

    @Builder
    public Cart(UserId userId) {
        this.id = CartId.of();
        this.userId = userId;
        this.cartItems = new ArrayList<>();
        this.totalPrice = new Price(0);
    }

    public void addItem(Item item, List<Integer> optionList) {

        CartItem existingItem = findMatchingItem(item.getId(), optionList);

        if (existingItem != null) {
            int itemIndex = this.cartItems.indexOf(existingItem);
            int newQuantity = existingItem.getQuantity() + 1;
            return;
        }
        Price newPrice = item.calculateItemPrice(optionList);

        CartItem newItem = CartItem.builder()
            .id(item.getId())
            .itemName(item.getName())
            .quantity(1)
            .selectedOptions(optionList)
            .price(newPrice)
            .build();

        this.cartItems.add(newItem);
        recalculateTotalPrice();
    }

    public void removeItem(int itemIndex) {
        findCartItemByIndex(itemIndex); // 인덱스 검증
        this.cartItems.remove(itemIndex);
        recalculateTotalPrice(); // 총액 재계산
    }

    private void recalculateTotalPrice() {
        Price newTotal = new Price(0); // Price.ZERO
        for (CartItem item : this.cartItems) {
            newTotal = newTotal.add(item.calculateItemPrice());
        }
        this.totalPrice = newTotal;
    }

    private CartItem findCartItemByIndex(int itemIndex) {
        if (itemIndex < 0 || itemIndex >= this.cartItems.size()) {
            throw new IndexOutOfBoundsException("Invalid item index for cart: " + itemIndex);
        }
        return this.cartItems.get(itemIndex);
    }

    private CartItem findMatchingItem(ItemId itemId, List<Integer> optionList) {
        return this.cartItems.stream()
            .filter(ci -> ci.matches(itemId, optionList))
            .findFirst()
            .orElse(null);
    }
}