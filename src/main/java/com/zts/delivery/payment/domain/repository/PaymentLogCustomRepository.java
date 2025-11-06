package com.zts.delivery.payment.domain.repository;

import com.zts.delivery.payment.domain.PaymentLog;
import com.zts.delivery.payment.domain.PaymentMethod;
import com.zts.delivery.payment.domain.PaymentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentLogCustomRepository {

    Page<PaymentLog> findFailLogs(PaymentType paymentType, PaymentMethod paymentMethod, int retryCount, boolean isSuccess, Pageable pageable);
}
