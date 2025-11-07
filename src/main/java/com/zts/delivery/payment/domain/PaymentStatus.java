package com.zts.delivery.payment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {

    READY("결제 생성"),
    IN_PROGRESS("승인 대기"),
    DONE("결제 완료"),
    ABORTED("결제 승인 실패"),
    CANCELED("결제 취소"),
    EXPIRED("유효 기간 만료");

    private final String description;
}
