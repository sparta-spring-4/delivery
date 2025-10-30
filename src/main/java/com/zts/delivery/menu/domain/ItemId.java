package com.zts.delivery.menu.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemId {
    @Column(name="item_id")
    private UUID id;

    public ItemId(UUID id) {
        this.id = id;
    }

    public static ItemId of(UUID id) {
        return new ItemId(id);
    }
}
