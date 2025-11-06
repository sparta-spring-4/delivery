package com.zts.delivery.order.domain.cart;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.global.persistence.common.DateAudit;
import com.zts.delivery.global.persistence.converter.PriceConverter;
import com.zts.delivery.menu.domain.Item;
import com.zts.delivery.user.domain.UserId;
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
    private Price price;

    @Builder
    public Cart(UserId userId) {
        this.id = CartId.of();
        this.userId = userId;
        this.cartItems = new ArrayList<>();
        this.price = new Price(0);
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

    // removeItem 메서드는 더 이상 항목을 제거하기 전에 itemIndex를 검증하지 않습니다. 제거된 검증 로직 'findCartItemByIndex(itemIndex)'는 유지하거나 범위를 확인하는 로직으로 대체하여 IndexOutOfBoundsException을 방지해야 합니다.
    public void removeItem(Item item , int itemIndex) {
        // isValidOptionIndices(item ,);
        this.cartItems.remove(itemIndex);
        calculateTotalPrice(); // 총액 재계산
    }

    private void calculateTotalPrice() {
        Price newTotal = new Price(0);
        for (CartItem item : this.cartItems) {
            newTotal = newTotal.add(item.calculateTotalPrice());
        }
        this.price = newTotal;
    }


    public void changeItemQuantity(int idx, boolean isAdding) {
        CartItem item = this.cartItems.get(idx);
        item.updateQuantity(isAdding);
        this.cartItems.set(idx, item);
        calculateTotalPrice();
    }


    public void changeItemOptions(int idx, Item item, List<Integer> options) {

        // 1. 해당 인덱스의 *기존* CartItem을 가져옵니다.
        CartItem oldCartItem = this.cartItems.get(idx);

        // 2. 검증 로직 (Item 객체가 필요합니다)
        if(!isValidOptionIndices(item, options)) {
            throw new IllegalArgumentException("Invalid option indices");
        }

        // 3. *새로운* 옵션으로 *새로운* CartItem 객체를 생성합니다.
        CartItem newCartItem = oldCartItem.chooseOptions(item, options);

        // 4. [핵심] 리스트에서 기존 객체를 새로운 객체로 교체합니다.
        this.cartItems.set(idx, newCartItem);

        // 5. [누락된 부분] 총액을 다시 계산합니다.
        calculateTotalPrice();
    }

    private boolean isValidOptionIndices(Item item, List<Integer> optionIndices) {
        int optionsSize = item.getItemOptions() != null ? item.getItemOptions().size() : 0;

        for (Integer index : optionIndices) {
            if (index < 0 || index >= optionsSize) {
                return false;
            }
        }
        return true;
    }
}