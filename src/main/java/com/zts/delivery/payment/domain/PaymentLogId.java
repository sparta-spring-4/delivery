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
public class PaymentLogId implements Serializable {
    @Column(length = 45, name = "payment_log_id")
    private UUID id;

    public PaymentLogId(UUID id) {
        this.id = id;
    }

    public static PaymentLogId of(UUID id) {
        return new PaymentLogId(id);
    }

    public static PaymentLogId of() {
        return new PaymentLogId(UUID.randomUUID());
    }
}
