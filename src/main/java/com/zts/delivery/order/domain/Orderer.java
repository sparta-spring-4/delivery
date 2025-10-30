package com.zts.delivery.order.domain;

import com.zts.delivery.user.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orderer {
    @Column(name = "orderer_id")
    private UserId id;

    @Column(name = "orderer_name")
    private String name;

    @Builder
    public  Orderer(UserId id, String name) {
        this.id = id;
        this.name = name;
    }
}
