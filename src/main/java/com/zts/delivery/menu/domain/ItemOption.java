package com.zts.delivery.menu.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemOption {

    @Column(nullable = false, length=60)
    private String optionName;

}