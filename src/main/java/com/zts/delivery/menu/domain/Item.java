package com.zts.delivery.menu.domain;

import com.zts.delivery.global.persistence.common.BaseEntity;
import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.global.persistence.converter.PriceConverter;
import com.zts.delivery.menu.infrastructure.converter.StockConverter;
import com.zts.delivery.store.StoreId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    @AttributeOverrides(
        @AttributeOverride(name = "id", column = @Column(name = "store_id", nullable = false))
    )
    private StoreId storeId;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "P_ITEM_OPTION", joinColumns = @JoinColumn(name = "item_id"))
    @OrderColumn(name = "option_idx")
    private List<ItemOption> itemOptions;

    @Convert(converter = PriceConverter.class)
    private Price price;

    @Column(nullable = false)
    private String name;

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
        this.status = Objects.requireNonNullElse(status, ItemStatus.UNLIMITED_STOCK); // 재고 기본 값은 무제한 재고
        setStock(stock);

    }

    public int getPriceValue() {
        return price.getValue();
    }

    public void setStock(Stock stock) {
        this.stock = stock;

        if (stock.getValue() == 0 || status == ItemStatus.OUT_OF_STOCK) {
            outOfStock = false;
        }
    }

    // 옵션 추가
    public void addOption(ItemOption itemOption) {
        this.itemOptions = Objects.requireNonNullElseGet(this.itemOptions, ArrayList::new);
        this.itemOptions.add(itemOption);
    }

    // 옵션 제거
    public void removeOption(ItemOption itemOption) {
        if (itemOptions == null) return;

        itemOptions.remove(itemOption);
    }

    // 옵션 순서 번호로 제거
    public void removeOption(int idx) {
        if (itemOptions == null) return;

        itemOptions.remove(idx);
    }
    // 전체 옵션 제거
    public void removeOptionAll() {
        if (itemOptions == null) return;

        itemOptions.forEach(o -> itemOptions.remove(o));
    }
    /**
     * 상품 수량 + 옵션 번호, 수량으로 금액 계산
     * @param itemCnt
     * @param options
     * @return
     */

    public Price getTotal(int itemCnt, Map<Integer, Integer> options) {
        Price totalPrice = price.add(price.multiply(itemCnt));

        if (itemOptions != null && options != null) {
            options.forEach((optionIdx, optionCnt) -> {
                ItemOption item = itemOptions.get(optionIdx);
                if (item == null) return;
                totalPrice.add(item.getPrice().multiply(optionCnt));
            });
        }

        return totalPrice;
    }
}
