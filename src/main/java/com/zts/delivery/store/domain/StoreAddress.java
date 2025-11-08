package com.zts.delivery.store.domain;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import lombok.*;

@ToString
@Getter
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreAddress {

    @Column(length = 100, nullable = false)
    private String address;
    private double latitude;
    private double longitude;

    @Builder
    public StoreAddress(String address, double latitude, double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected static StoreAddress of(String address) {
        return StoreAddress.builder()
                .address(address)
                .build();
    }
}
