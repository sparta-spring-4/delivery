package com.zts.delivery.store.domain;

import com.zts.delivery.user.UserId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;

@ToString
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Owner {

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "owner_id"))
    private UserId id;

    @Column(name = "owner_name")
    private String name;

    @Builder
    public Owner(UserId id, String name)  {
        this.id = id;
        this.name = name;
    }
}
