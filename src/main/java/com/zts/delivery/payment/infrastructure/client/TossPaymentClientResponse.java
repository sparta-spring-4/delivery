package com.zts.delivery.payment.infrastructure.client;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

public record TossPaymentClientResponse(
        String mId,
        String lastTransactionKey,
        String paymentKey,
        String orderId,
        String orderName,
        int taxExemptionAmount,
        String status,
        ZonedDateTime requestedAt,
        ZonedDateTime approvedAt,
        boolean useEscrow,
        boolean cultureExpense,
        Card card,
        Object virtualAccount,
        Object transfer,
        Object mobilePhone,
        Object giftCertificate,
        Object cashReceipt,
        List<Object> cashReceipts,
        Object discount,
        List<Cancel> cancels,
        Object secret,
        String type,
        EasyPay easyPay,
        String country,
        Object failure,
        boolean isPartialCancelable,
        Receipt receipt,
        Checkout checkout,
        String currency,
        int totalAmount,
        int balanceAmount,
        int suppliedAmount,
        int vat,
        int taxFreeAmount,
        Object metadata,
        String method,
        String version
) {
    public record Card(
            String issuerCode,
            String acquirerCode,
            String number,
            int installmentPlanMonths,
            boolean isInterestFree,
            Object interestPayer, // null
            String approveNo,
            boolean useCardPoint,
            String cardType,
            String ownerType,
            String acquireStatus,
            int amount
    ) {
    }

    public record EasyPay(
            String provider,
            int amount,
            int discountAmount
    ) {
    }

    public record Receipt(
            String url
    ) {
    }

    public record Checkout(
            String url
    ) {
    }

    public record Cancel(
            String transactionKey,
            String cancelReason,
            long taxExemptionAmount,
            LocalDateTime canceledAt,
            long transferDiscountAmount,
            long easyPayDiscountAmount,
            String receiptKey,
            long cancelAmount,
            long taxFreeAmount,
            long refundableAmount,
            String cancelStatus,
            String cancelRequestId
    ) {
    }
}