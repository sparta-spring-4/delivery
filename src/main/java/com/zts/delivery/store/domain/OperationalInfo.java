package com.zts.delivery.store.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@ToString
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OperationalInfo {
    private String operationHours;
    private String closeDays;
    private String deliveryAddress;

    @Builder
    public OperationalInfo(String operationHours, String closeDays, String deliveryAddress) {
        this.operationHours = operationHours;
        this.closeDays = closeDays;
        this.deliveryAddress = deliveryAddress;
    }
}
