package com.zts.delivery.order.domain.cart;

import com.zts.delivery.global.infrastructure.execption.ApplicationException;
import com.zts.delivery.global.infrastructure.execption.ErrorCode;
import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.global.persistence.common.DateAudit;
import com.zts.delivery.global.persistence.converter.PriceConverter;
import com.zts.delivery.menu.domain.Item;
import com.zts.delivery.user.domain.UserId;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

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
    private Price cartTotalPrice;

    @Builder
    public Cart(UserId userId) {
        this.id = CartId.of();
        this.userId = userId;
        this.cartItems = new ArrayList<>();
        this.cartTotalPrice = new Price(0);
    }

    public void addItem(Item item, List<Integer> optionList) {
        CartItem newItem = CartItem.builder()
            .id(item.getId())
            .quantity(1)
            .selectedOptions(optionList)
            .price(item.getPrice())
            .build();

        this.cartItems.add(newItem);
        calculateTotalPrice();
    }

    public void removeItem(int itemIndex) {
        isValidItemIndex(itemIndex);
        this.cartItems.remove(itemIndex);
        calculateTotalPrice();
    }

    public void calculateTotalPrice() {
        this.cartTotalPrice = cartItems.stream().map(CartItem::getTotalPrice)
            .reduce(new Price(0), Price::add);
    }

    public void changeItemQuantity(int idx, boolean isAdding) {
        isValidItemIndex(idx);
        CartItem item = this.cartItems.get(idx);
        CartItem cartItem = item.updateQuantity(isAdding);
        calculateTotalPrice();
    }

    public void changeItemOptions(int idx, Item item, List<Integer> options) {

        isValidOptionIndices(item, options);
        CartItem oldCartItem = this.cartItems.get(idx);

        oldCartItem.updateOptions(item, options);
        calculateTotalPrice();
    }

    public void deleteCartItem(int idx) {
        isValidItemIndex(idx);
        this.cartItems.remove(idx);
        calculateTotalPrice();
    }

    private void isValidOptionIndices(Item item, List<Integer> optionIndices) {
        int optionsSize = item.getItemOptions() != null ? item.getItemOptions().size() : 0;

        for (Integer index : optionIndices) {
            if (index < 0 || index >= optionsSize) {
                throw new ApplicationException(ErrorCode.REQUEST_VALIDATION_ERROR);
            }
        }
    }

    private void isValidItemIndex(int itemIndex) {
        if(itemIndex < 0 || itemIndex >= this.cartItems.size()) {
            throw new ApplicationException(ErrorCode.REQUEST_VALIDATION_ERROR);
        }
    }
}