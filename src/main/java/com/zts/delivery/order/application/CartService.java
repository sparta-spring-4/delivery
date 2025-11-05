package com.zts.delivery.order.application;


import com.zts.delivery.infrastructure.execption.ApplicationException;
import com.zts.delivery.infrastructure.execption.ErrorCode;
import com.zts.delivery.menu.domain.Item;
import com.zts.delivery.menu.domain.ItemRepository;
import com.zts.delivery.order.domain.cart.Cart;
import com.zts.delivery.order.domain.cart.CartItem;
import com.zts.delivery.order.domain.cart.CartRepository;
import com.zts.delivery.order.presentation.dto.CartItemOptionUpdateRequest;
import com.zts.delivery.order.presentation.dto.CartRequest;
import com.zts.delivery.order.presentation.dto.CartResponse;
import com.zts.delivery.order.presentation.dto.CartUpdateRequest;
import com.zts.delivery.user.domain.UserId;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public CartResponse create(CartRequest req, UserId userId) {

        Item item = itemRepository.findById(req.itemId())
            .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));

        validateItem(item, req.options());

        Cart t_cart = cartRepository.findByUserId(userId)
            .orElseGet(() -> createCart(userId));

        t_cart.addItem(item, req.options());

        Cart p_cart = cartRepository.save(t_cart);

        return CartResponse.of(p_cart);
    }

    @Transactional
    public CartResponse update(CartUpdateRequest request, UserId userId) {

        boolean adding = request.isAdding();
        int idx = request.cartItemIndex();

        Cart t_cart = cartRepository.findById(request.cartId())
            .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));

        if (!t_cart.getUserId().equals(userId)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }

        CartItem cartItem = t_cart.getCartItems().get(idx);
        if (cartItem.getQuantity() == 1 && !adding) {
            delete(userId);
        }

        t_cart.changeItemQuantity(idx, adding);

        Cart p_cart = cartRepository.save(t_cart);

        return CartResponse.of(p_cart);
    }


    @Transactional
    public CartResponse read(UserId userId) {
        Cart cart = findByUserId(userId);

        return CartResponse.of(cart);
    }

    @Transactional
    public void delete(UserId userId) {
        Cart t_cart = findByUserId(userId);

        cartRepository.delete(t_cart);
    }

    public Cart createCart(UserId userId) {
        Cart t_cart = Cart.builder()
            .userId(userId)
            .build();

        return cartRepository.save(t_cart);
    }

    private Cart findByUserId(UserId userId) {
        return cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));
    }

    private void validateItem(Item item, List<Integer> optionList) {

        if (!item.isActive() || item.isOutOfStock()) {
            throw new IllegalArgumentException("Not a sellable item.");
        }

        int maxIndex = (item.getItemOptions() != null) ? item.getItemOptions().size() - 1 : -1;
        if (optionList != null) {
            for (Integer idx : optionList) {
                if (idx < 0 || idx > maxIndex) {
                    throw new IllegalArgumentException("Invalid option index: " + idx);
                }
            }
        }
    }

    @Transactional
    public CartResponse updateItemOptions(@Valid CartItemOptionUpdateRequest req, UserId userId) {
        // Find cart by ID
        Cart t_cart = cartRepository.findById(req.cartId())
            .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));
        if (!t_cart.getUserId().equals(userId)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }
        CartItem cartItem = t_cart.getCartItems().get(req.cartItemIndex());
        Item item = itemRepository.findById(cartItem.getId())
            .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));
        t_cart.changeItemOptions(cartItem, item, req.newOptionIndices());

        Cart p_cart = cartRepository.save(t_cart);
        return CartResponse.of(p_cart);
    }
}
