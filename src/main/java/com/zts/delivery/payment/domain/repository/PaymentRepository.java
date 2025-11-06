package com.zts.delivery.payment.domain.repository;

import com.zts.delivery.payment.domain.Payment;
import com.zts.delivery.payment.domain.PaymentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, PaymentId> {
}
