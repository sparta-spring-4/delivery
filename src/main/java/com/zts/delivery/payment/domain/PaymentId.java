package com.zts.delivery.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@ToString
@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PaymentId implements Serializable {
    @Column(length = 45, name = "payment_id")
    private UUID id;

    public PaymentId(UUID id) {
        this.id = id;
    }

    public static PaymentId of(UUID id) {
        return new PaymentId(id);
    }

    public static PaymentId of() {
        return new PaymentId(UUID.randomUUID());
    }
}
