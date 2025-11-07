package com.zts.delivery.payment.domain.repository;

import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.payment.domain.Payment;
import com.zts.delivery.payment.domain.PaymentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, PaymentId> {
    Optional<Payment> findByOrderId(OrderId orderId);
}
