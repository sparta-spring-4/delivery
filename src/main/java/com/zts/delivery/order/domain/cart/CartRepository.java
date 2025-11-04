package com.zts.delivery.order.domain.cart;

import com.zts.delivery.user.UserId;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends CrudRepository<Cart, CartId> {
    Optional<Cart> findByUserId(UserId userId);

}
