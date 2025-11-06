package com.zts.delivery.payment.domain.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zts.delivery.payment.domain.PaymentLog;
import com.zts.delivery.payment.domain.PaymentMethod;
import com.zts.delivery.payment.domain.PaymentType;
import com.zts.delivery.payment.domain.QPaymentLog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class PaymentLogCustomRepositoryImpl implements PaymentLogCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PaymentLog> findFailLogs(PaymentType paymentType, PaymentMethod paymentMethod, int retryCount, boolean isSuccess, Pageable pageable) {
        QPaymentLog paymentLog = QPaymentLog.paymentLog;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(paymentLog.paymentType.eq(paymentType))
                .and(paymentLog.paymentMethod.eq(paymentMethod))
                .and(paymentLog.retryCount.loe(retryCount))
                .and(paymentLog.isSuccess.eq(isSuccess));

        List<PaymentLog> content = queryFactory.selectFrom(paymentLog)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(paymentLog.count())
                .from(paymentLog)
                .where(builder)
                .fetchOne();

        if (total == null) {
            total = 0L;
        }

        return new PageImpl<>(content, pageable, total);
    }

}
