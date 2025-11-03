package com.zts.delivery.store;

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
public class StoreId {
    @Column(length=45, name="store_id")
    private UUID id;

    public StoreId(UUID id) {
        this.id = id;
    }

    public static StoreId of() {
        return StoreId.of(java.util.UUID.randomUUID());
    }

    public static StoreId of(UUID id) {
        return new StoreId(id);
    }
}
