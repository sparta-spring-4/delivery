package com.zts.delivery.payment.domain.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zts.delivery.payment.domain.PaymentLog;
import com.zts.delivery.payment.domain.PaymentMethod;
import com.zts.delivery.payment.domain.PaymentType;
import com.zts.delivery.payment.domain.QPaymentLog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class PaymentLogCustomRepositoryImpl implements PaymentLogCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PaymentLog> findFailLogs(PaymentType paymentType, PaymentMethod paymentMethod, int retryCount, boolean isSuccess, Pageable pageable) {
        QPaymentLog paymentLog = QPaymentLog.paymentLog;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(paymentLog.paymentType.eq(paymentType))
                .and(paymentLog.paymentMethod.eq(paymentMethod))
                .and(paymentLog.retryCount.lt(retryCount))
                .and(paymentLog.isSuccess.eq(isSuccess));

        return queryFactory.selectFrom(paymentLog)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

}
