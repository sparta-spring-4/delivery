package com.zts.delivery.store.domain;

import com.zts.delivery.user.domain.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@ToString
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Owner {
    @Column(name = "owner_id")
    private UserId id;

    @Column(name = "owner_name")
    private String name;

    @Builder
    public Owner(UserId id, String name)  {
        this.id = id;
        this.name = name;
    }
}
