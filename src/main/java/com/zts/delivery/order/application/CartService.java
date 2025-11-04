package com.zts.delivery.order.application;


import com.zts.delivery.menu.domain.Item;
import com.zts.delivery.menu.domain.ItemId;
import com.zts.delivery.menu.domain.ItemRepository;
import com.zts.delivery.menu.domain.ItemStatus;
import com.zts.delivery.order.domain.cart.Cart;
import com.zts.delivery.order.domain.cart.CartItem;
import com.zts.delivery.order.domain.cart.CartRepository;
import com.zts.delivery.order.presentation.dto.CartRequest;
import com.zts.delivery.order.presentation.dto.CartResponse;
import com.zts.delivery.user.domain.UserId;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public CartResponse addCartItem(CartRequest request, UUID userId) {

        Item item = itemRepository.findById(ItemId.of(request.itemId()))
            .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        validateItemBeforeAdd(item, request.options());

        Cart t_cart = cartRepository.findByUserId(UserId.of(userId))
            .orElseGet(() -> createCart(userId));

        t_cart.addItem(item, request.options());

        Cart p_cart = cartRepository.save(t_cart);

        return CartResponse.from(p_cart);
    }

    public Cart createCart(UUID userId) {
        Cart cart = Cart.builder()
            .userId(UserId.of(userId))
            .build();

        return cartRepository.save(cart);
    }

    private void validateItemBeforeAdd(Item item, List<Integer> optionList) {

        if (!item.isActive() || item.isOutOfStock()) {
            throw new IllegalArgumentException("Not a sellable item.");
        }

        int maxIndex = item.getItemOptions().size() - 1;
        if (optionList != null) {
            for (Integer idx : optionList) {
                if (idx < 0 || idx > maxIndex) {
                    throw new IllegalArgumentException("Invalid option index: " + idx);
                }
            }
        }
    }
}
