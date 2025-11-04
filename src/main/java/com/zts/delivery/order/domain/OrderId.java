package com.zts.delivery.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class OrderId implements Serializable {
    private UUID id;

    public OrderId(UUID id) {
        this.id = id;
    }

    public static OrderId of(UUID id) {
        return new OrderId(id);
    }
}
