package com.zts.delivery.order.domain;

import com.zts.delivery.user.domain.UserId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, OrderId> {
    Optional<Order> findByIdAndOrderer_Id(OrderId id, UserId ordererId);

     List<Order> findAllByOrderer_Id(UserId ordererId);

    @Query("SELECT o FROM Order o WHERE o.orderer.id.id = :userUuid")
    List<Order> findAllByUserUuid(@Param("userUuid") UUID userUuid);
}
