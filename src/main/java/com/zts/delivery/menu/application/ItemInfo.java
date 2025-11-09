package com.zts.delivery.menu.application;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.menu.domain.*;
import com.zts.delivery.store.domain.StoreId;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ItemInfo {

    private ItemId id;
    private StoreId storeId;
    private List<ItemOption> itemOptions;
    private Price price;
    private String name;
    private ItemStatus status;
    private Stock stock;

    public static ItemInfo of(Item item) {
        return ItemInfo.builder()
                .id(item.getId())
                .name(item.getName())
                .storeId(item.getStoreId())
                .itemOptions(item.getItemOptions())
                .price(item.getPrice())
                .status(item.getStatus())
                .stock(item.getStock())
                .build();
    }
}
