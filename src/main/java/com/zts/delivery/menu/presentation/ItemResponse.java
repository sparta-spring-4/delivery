package com.zts.delivery.menu.presentation;

import com.zts.delivery.menu.application.ItemInfo;
import com.zts.delivery.menu.domain.ItemOption;
import com.zts.delivery.menu.domain.ItemStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class ItemResponse {

    private UUID id;
    private UUID storeId;
    private List<ItemOptionResponse> itemOptions;
    private int price;
    private String name;
    private ItemStatus status;
    private int stock;

    public static ItemResponse of(ItemInfo item) {
        List<ItemOptionResponse> toItemOptionResponse = item.getItemOptions().stream()
                .map(ItemOptionResponse::of)
                .toList();
        return ItemResponse.builder()
                .id(item.getId().getId())
                .storeId(item.getStoreId().getId())
                .name(item.getName())
                .itemOptions(toItemOptionResponse)
                .price(item.getPrice().getValue())
                .status(item.getStatus())
                .stock(item.getStock().getValue())
                .build();
    }

    @Getter
    @Builder
    public static class ItemOptionResponse {
        private String name;
        private int price;

        public static ItemOptionResponse of(ItemOption itemOption) {
            return ItemOptionResponse.builder()
                    .name(itemOption.getName())
                    .price(itemOption.getPrice().getValue())
                    .build();
        }
    }
}
