package com.zts.delivery.menu.application;

import com.zts.delivery.menu.domain.Item;
import com.zts.delivery.menu.domain.ItemRepository;
import com.zts.delivery.store.domain.StoreId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.zts.delivery.menu.domain.ItemStatus.IN_STOCK;
import static com.zts.delivery.menu.domain.ItemStatus.UNLIMITED_STOCK;

@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public List<ItemInfo> findItemsBy(StoreId storeId) {
        List<Item> items = itemRepository.findAllByStoreIdAndStatusInAndActive(storeId, List.of(IN_STOCK, UNLIMITED_STOCK), true);
        return items.stream()
                .map(ItemInfo::of)
                .toList();
    }
}
