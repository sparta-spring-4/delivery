package com.zts.delivery.payment.domain.repository;

import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.payment.domain.PaymentLog;
import com.zts.delivery.payment.domain.PaymentLogId;
import com.zts.delivery.payment.domain.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentLogRepository extends JpaRepository<PaymentLog, PaymentLogId>, PaymentLogCustomRepository {
    Optional<PaymentLog> findByOrderId(OrderId orderId);

    Optional<PaymentLog> findByOrderIdAndPaymentMethod(OrderId orderId, PaymentMethod paymentMethod);
}
