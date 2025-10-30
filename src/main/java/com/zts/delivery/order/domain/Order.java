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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.List;
import java.util.UUID;
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
    public Order(Orderer orderer, List<OrderItem> orderItems, DeliveryInfo deliveryInfo, OrderStatus status) {
        this.orderer = orderer;
        this.deliveryInfo = deliveryInfo;
        this.status = status;
        setOrderItems(orderItems);
        calculateTotalOrderPrice();
    }

    private void setOrderItems(List<OrderItem> orderItems) {
        // if (orderItems == null || orderItems.isEmpty()) {
        //     throw new Exception("Order must have at least one order item.");
        // }

        this.orderItems = orderItems;
    }

    private void calculateTotalOrderPrice() {
        this.totalOrderPrice = new Price(orderItems.stream().mapToInt(x -> x.getTotalPrice().getValue()).sum());
    }

    // 주문 접수
    public void  orderAccept() {
        this.status = OrderStatus.ORDER_ACCEPT;

        // 결제 요청 이벤트 발생 시키기
        // Events.trigger(new OrderAcceptEvent(id));
    }

    // 주문 취소
    public void cancel() {
        // 입금 확인 전이면 주문 취소, 입금 후 주문 접수 후 5분 이내라면 상태라면 환불
        if (this.status == OrderStatus.ORDER_ACCEPT) {
            this.status = OrderStatus.ORDER_CANCEL;
        } else if (getCreatedAt() != null && java.time.LocalDateTime.now().isBefore(getCreatedAt().plusMinutes(5L))) {
            this.status = OrderStatus.ORDER_REFUND;
        }
    }

    /**
     * 배송 시작
     * 배송 시작은 입금 확인 후 진행, 그러나 배송지가 매장에서 배송 가능한 지역이 아니라면 배송 불가 함
     * 매장별 배송 불가 지역 체크 필요, 그러나 이 기능은 다른 도메인 기능이 필요하므로 도메인 서비스로 추가, 단순히 도메인 서비스에 주문 도메인의 delivery상태 변경 로직은 실행
     */
    public void delivery() {
        if (this.status != OrderStatus.PAYMENT_CONFIRM) {
            return;
        }

        this.status = OrderStatus.DELIVERING;
    }


}
