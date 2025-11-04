package com.zts.delivery.order.domain.cart;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.global.persistence.converter.PriceConverter;
import com.zts.delivery.user.domain.UserId;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@ToString
@Getter
@Entity
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Cart {
    @EmbeddedId
    private CartId id;

    @Embedded
    private UserId userId;

    @Embedded
    private CartItem item;

    private int quantity;

    @Convert(converter = PriceConverter.class)
    private Price totalPrice;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public Cart(CartId cartId, UserId userId, CartItem item, int quantity) {
        this.userId = userId;
        this.quantity = quantity;
        setCartItem(item);
        calculateTotalPrice();
    }

    public static Cart create(UserId userId, CartItem item, int quantity) {

        Cart cart = Cart.builder()
            .userId(userId)
            .item(item)
            .quantity(quantity)
            .build();

        cart.id = CartId.of();

        return cart;
    }


    // 장바구니 상품이 없다면 담을 수 없음
    private void setCartItem(CartItem item) {
        // if (item == null) throw new CartItemNotFoundException();

        this.item = item;
    }

    // 장바구니 상품별 총 합계
    private void calculateTotalPrice() {
        if (item == null) return;

        totalPrice = new Price(item.getItemPrice().getValue() * quantity);
    }
}
