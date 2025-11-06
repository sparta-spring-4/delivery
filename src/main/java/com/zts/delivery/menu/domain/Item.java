package com.zts.delivery.menu.domain;

import com.zts.delivery.global.persistence.common.BaseEntity;
import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.global.persistence.converter.PriceConverter;
import com.zts.delivery.menu.infrastructure.converter.StockConverter;
import com.zts.delivery.store.StoreId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {

    @EmbeddedId
    private ItemId id;

    @Embedded
    @AttributeOverride(name = "store_id", column = @Column(name = "store_id", nullable = false))
    private StoreId storeId;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "P_ITEM_OPTION", joinColumns = @JoinColumn(name = "item_id"))
    @OrderColumn(name = "option_idx")
    private List<ItemOption> itemOptions;

    @Convert(converter = PriceConverter.class)
    private Price price;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus status;

    private boolean active;

    @Convert(converter = StockConverter.class)
    @Column(name = "stock")
    private Stock stock;

    @Transient
    private boolean outOfStock;

    @Builder
    public Item(StoreId storeId, ItemId id, Price price, String name, ItemStatus status, Stock stock, List<ItemOption> itemOptions) {
        this.storeId = storeId;
        this.id = Objects.requireNonNullElse(id, ItemId.of());
        this.price = price;
        this.name = name;
        this.itemOptions = itemOptions;
        this.status = Objects.requireNonNullElse(status, ItemStatus.UNLIMITED_STOCK);
        this.active = true;
        setStock(stock);
    }

    // 옵션 추가
    public void addOption(ItemOption itemOption) {
        itemOptions = Objects.requireNonNullElseGet(itemOptions, ArrayList::new);
        itemOptions.add(itemOption);
    }

    // 옵션 변경

    public void updateOption(int index, ItemOption itemOption) {
        if (itemOptions == null) return;

        itemOptions.set(index, itemOption);
    }

    // 옵션 제거
    public void removeOption(ItemOption itemOption) {
        if (itemOptions == null) return;

        itemOptions.remove(itemOption);
    }


    public void setStock(Stock stock) {
        this.stock = stock;

        if (stock.getValue() == 0 || status == ItemStatus.OUT_OF_STOCK) {
            outOfStock = true;
        }
    }

    public Price getOptionsPrice(List<Integer> selectedOptions) {
        if (selectedOptions == null || selectedOptions.isEmpty()) {
            return new Price(0);
        }

        Price totalOptionsPrice = new Price(0);
        for (Integer idx : selectedOptions) {
            totalOptionsPrice.add(itemOptions.get(idx).getPrice());
        }

        return totalOptionsPrice;
    }
}
