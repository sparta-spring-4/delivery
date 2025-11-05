package com.zts.delivery.user.presentation.dto;

import com.zts.delivery.user.application.dto.UserAddressInfo;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserAddressResponse(
        UUID id,
        String alias, // 장소에 대한 가명(e.g. 집, 회사)
        String postalCode, // 우편 번호
        String stateOrProvince, // 구
        String city, // 시
        String street, // 도로명 주소
        String detailAddress // 상세 주소
) {
    public static UserAddressResponse of(UserAddressInfo userAddress) {
        return UserAddressResponse.builder()
                .id(userAddress.id())
                .alias(userAddress.alias())
                .postalCode(userAddress.postalCode())
                .stateOrProvince(userAddress.stateOrProvince())
                .city(userAddress.city())
                .street(userAddress.street())
                .detailAddress(userAddress.detailAddress())
                .build();
    }
}
