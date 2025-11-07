package com.zts.delivery.user.application.dto;

import com.zts.delivery.user.domain.UserAddress;
import lombok.Builder;

import java.util.UUID;

@Builder
public record RegisterUserAddress(
        String alias, // 장소에 대한 가명(e.g. 집, 회사)
        String postalCode, // 우편 번호
        String stateOrProvince, // 구
        String city, // 시
        String street, // 도로명 주소
        String detailAddress // 상세 주소
) {
    public UserAddress toUserAddress(UUID addressId) {
        return UserAddress.builder()
                .id(addressId)
                .alias(alias)
                .postalCode(postalCode)
                .stateOrProvince(stateOrProvince)
                .city(city)
                .street(street)
                .detailAddress(detailAddress)
                .build();
    }
}
