package com.zts.delivery.menu.domain;

import com.zts.delivery.global.persistence.common.BaseEntity;
import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.global.persistence.converter.PriceConverter;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {
    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.UUID)
    private ItemId id;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="P_ITEM_OPTION", joinColumns = @JoinColumn(name="item_id"))
    @OrderColumn(name="option_idx")
    private List<ItemOption> itemOptions;

    @Convert(converter = PriceConverter.class)
    private Price price;

    @Column(nullable = false)
    private String name;

    private boolean active;
}
