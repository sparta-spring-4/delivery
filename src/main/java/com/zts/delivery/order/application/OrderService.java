package com.zts.delivery.order.application;

import com.zts.delivery.infrastructure.execption.ApplicationException;
import com.zts.delivery.infrastructure.execption.ErrorCode;
import com.zts.delivery.menu.domain.ItemId;
import com.zts.delivery.menu.domain.ItemOption;
import com.zts.delivery.menu.domain.ItemRepository;
import com.zts.delivery.order.domain.DeliveryInfo;
import com.zts.delivery.order.domain.Order;
import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.order.domain.OrderItem;
import com.zts.delivery.order.domain.OrderItemOption;
import com.zts.delivery.order.domain.OrderRepository;
import com.zts.delivery.order.domain.Orderer;
import com.zts.delivery.order.domain.cart.Cart;
import com.zts.delivery.order.domain.cart.CartId;
import com.zts.delivery.order.domain.cart.CartItem;
import com.zts.delivery.order.domain.cart.CartRepository;
import com.zts.delivery.order.presentation.dto.OrderRequest;
import com.zts.delivery.order.presentation.dto.OrderResponse;
import com.zts.delivery.order.presentation.dto.OrderStatusChangeRequest;
import com.zts.delivery.user.domain.User;
import com.zts.delivery.user.domain.UserId;
import com.zts.delivery.user.infrastructure.security.UserPrincipal;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public OrderResponse create(OrderRequest req, UserId userId) {
        // cart로부터 정보 가져오기
        Cart cart = cartRepository.findById(CartId.of(req.cartId()))
            .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));
        // 카트의 user와 동일한 유저인지 확인하기
        if (!cart.getUserId().equals(userId)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }
        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new ApplicationException(ErrorCode.BAD_REQUEST, "Cart is empty");
        }
        Order t_order = toOrder(req, userId, cart);
        Order p_order = orderRepository.save(t_order);

        // 이벤트 발생
        return OrderResponse.of(p_order);

    }

    @Transactional
    public void reject(OrderId orderId, UserId userId) {
        Order t_order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));
        t_order.cancel();
        Order p_order = orderRepository.save(t_order);
    }



    @Transactional
    public void cancel(OrderId orderId, UserId userId) {
        Order t_order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));
        t_order.cancel();
        Order p_order = orderRepository.save(t_order);
    }

    private List<OrderItem> convertCartItemsToOrderItems(Cart cart) {
        return cart.getCartItems().stream()
            .map(cartItem -> new OrderItem(
                cartItem.getId(),
                cartItem.getItemName(),
                cartItem.getQuantity(),
                convertCartItemsToOrderItemOptions(cartItem.getSelectedOptions(), cartItem.getId()),
                cartItem.getTotalPrice()
            ))
            .toList();
    }

    private List<OrderItemOption> convertCartItemsToOrderItemOptions(List<Integer> optionIndices, ItemId itemid) {
        List<ItemOption> itemOptions = itemRepository.findById(itemid)
            .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND))
            .getItemOptions();
        return optionIndices.stream()
            .map(index -> {
                if (index < 0 || index >= itemOptions.size()) {
                    throw new ApplicationException(ErrorCode.BAD_REQUEST, "Invalid option index: " + index);
                }
                ItemOption selectedOption = itemOptions.get(index);
                return new OrderItemOption(
                    selectedOption.getName(),
                    selectedOption.getPrice()
                );
            })
            .toList();
    }

    public Order toOrder(OrderRequest req, UserId userId, Cart cart) {
        Orderer orderer = Orderer.builder()
            .id(userId)
            .name(req.ordererName())
            .build();
        DeliveryInfo deliveryInfo = DeliveryInfo.builder()
            .address(req.address())
            .memo(req.memo())
            .build();
        List<OrderItem> orderItems = convertCartItemsToOrderItems(cart);

        return Order.create(
            orderer,
            orderItems,
            deliveryInfo,
            cart.getCartTotalPrice()
        );
    }

    @Transactional
    public void acceptOrder(OrderId orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, "주문을 찾을 수 없습니다."));

        order.accept();
    }

    @Transactional
    public void updateToDelivering(OrderId orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, "주문을 찾을 수 없습니다."));
        order.delivery();
    }

    @Transactional
    public void updateToDelivered(OrderId orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, "주문을 찾을 수 없습니다."));
        order.delivered();
    }

    public void delete(OrderId id, UserPrincipal user) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));
        if (!order.getOrderer().getId().equals(user.userId())) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }
        order.markAsDeleted(user.username());
    }

    public OrderResponse read(OrderId id, UserPrincipal user) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));
        if (!order.getOrderer().getId().equals(user.userId())) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }
        return OrderResponse.of(order);
    }

    public Page<OrderResponse> readAll(UserPrincipal user, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAllByOrderer_Id_Id(
            user.userId().getId(),
            pageable
        );
        return orderPage.map(OrderResponse::of);
    }
}
