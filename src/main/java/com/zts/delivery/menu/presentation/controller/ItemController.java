package com.zts.delivery.menu.presentation.controller;

import com.zts.delivery.menu.application.ItemInfo;
import com.zts.delivery.menu.application.ItemService;
import com.zts.delivery.menu.presentation.ItemResponse;
import com.zts.delivery.store.domain.StoreId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "메뉴 API", description = "메뉴를 생성 / 조회 합니다.")
@RestController
@RequestMapping("/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @Operation(summary = "메뉴 조회", description = "storeId로 가게 메뉴를 조회합니다.")
    @GetMapping
    public List<ItemResponse> getItemsBy(UUID storeId) {
        List<ItemInfo> itemInfos = itemService.findItemsBy(StoreId.of(storeId));
        return itemInfos.stream()
                .map(ItemResponse::of)
                .toList();
    }
}
