package com.zts.delivery.order.application;

import com.zts.delivery.infrastructure.execption.ApplicationException;
import com.zts.delivery.infrastructure.execption.ErrorCode;
import com.zts.delivery.menu.domain.ItemId;
import com.zts.delivery.menu.domain.ItemOption;
import com.zts.delivery.menu.domain.ItemRepository;
import com.zts.delivery.order.domain.DeliveryInfo;
import com.zts.delivery.order.domain.Order;
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
import com.zts.delivery.user.domain.UserId;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
