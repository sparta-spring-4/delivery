package com.zts.delivery.payment.domain.repository;

import com.zts.delivery.payment.domain.PaymentLog;
import com.zts.delivery.payment.domain.PaymentMethod;
import com.zts.delivery.payment.domain.PaymentType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentLogCustomRepository {

    List<PaymentLog> findFailLogs(PaymentType paymentType, PaymentMethod paymentMethod, int retryCount, boolean isSuccess, Pageable pageable);
}
