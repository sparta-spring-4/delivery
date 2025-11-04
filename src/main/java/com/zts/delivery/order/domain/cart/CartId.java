package com.zts.delivery.order.domain.cart;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@ToString
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class CartId implements Serializable {
    @Column(columnDefinition = "BINARY(16)" ,length = 45, name = "cart_id" )
    private UUID id;

    public CartId(UUID id) {
        this.id = id;
    }

    public static CartId of() {
        return CartId.of(UUID.randomUUID());
    }

    public static CartId of(UUID id) {
        return new CartId(id);
    }
}
