package com.zts.delivery.menu.domain;

import com.zts.delivery.store.domain.StoreId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, ItemId> {

    List<Item> findByIdIn(Collection<ItemId> ids);

    @EntityGraph(attributePaths = {"itemOptions"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Item> findAllByStoreIdAndStatusInAndActive(StoreId storeId, List<ItemStatus> statuses, boolean active);

}
