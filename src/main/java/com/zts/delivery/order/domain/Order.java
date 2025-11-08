package com.zts.delivery.order.domain;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.global.persistence.common.BaseEntity;
import com.zts.delivery.global.persistence.converter.PriceConverter;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
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
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "P_ORDER")
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @EmbeddedId
    private OrderId id;

    @Version
    private Long version;

    @Embedded
    private Orderer orderer;

    @Embedded
    private DeliveryInfo deliveryInfo;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "P_ORDER_ITEM", joinColumns = @JoinColumn(name = "order_id"))
    @OrderColumn(name = "item_idx")
    private List<OrderItem> orderItems;

    @Convert(converter = PriceConverter.class)
    private Price totalOrderPrice;

    @Enumerated(EnumType.STRING)
    @Column(length = 45)
    private OrderStatus status;

    @Builder
    public Order(Orderer orderer, List<OrderItem> orderItems, DeliveryInfo deliveryInfo, Price totalOrderPrice) {
        this.orderer = orderer;
        this.deliveryInfo = deliveryInfo;
        this.status = OrderStatus.ORDER_CREATED;
        this.totalOrderPrice = totalOrderPrice;
        this.orderItems = orderItems;
    }

    public static Order create(Orderer orderer, List<OrderItem> orderItems, DeliveryInfo deliveryInfo, Price cartTotalPrice) {

        Order order = Order.builder()
            .orderer(orderer)
            .deliveryInfo(deliveryInfo)
            .orderItems(orderItems)
            .totalOrderPrice(cartTotalPrice)
            .build();

        order.id = OrderId.of();

        return order;
    }

    public void cancel() {
        if (getCreatedAt() != null && LocalDateTime.now().isBefore(getCreatedAt().plusMinutes(5L))) {
            this.status = OrderStatus.ORDER_CANCEL;
        }
    }

    public void paymentComplete() {
        if (this.status != OrderStatus.ORDER_CREATED) {
            return;
        }
        this.status = OrderStatus.PAYMENT_CONFIRM;
    }

    public void accept() {
        if (this.status != OrderStatus.PAYMENT_CONFIRM) {
            return;
        }
        this.status = OrderStatus.PREPARING;
    }

    public void delivery() {
        if (this.status != OrderStatus.PREPARING) {
            return;
        }
        this.status = OrderStatus.DELIVERING;
    }

    public void delivered() {
        if (this.status != OrderStatus.DELIVERING) {
            return;
        }
        this.status = OrderStatus.DELIVERED;
    }

    public void refundComplete() {
        if (this.status == OrderStatus.ORDER_CANCEL || this.status == OrderStatus.PAYMENT_CONFIRM) {
            this.status = OrderStatus.ORDER_REFUND;
        }
        return;
    }
}
