package com.zts.delivery.menu.domain;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, ItemId> {

    List<Item> findByIdIn(Collection<ItemId> ids);
}
