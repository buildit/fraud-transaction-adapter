package com.wipro.wega.fraud_transaction_adapter.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Canonical credit transaction parsed from the inbound COBOL fixed-width record.
 * Serialised to JSON as the request body for the credit-score service.
 */
public record CreditTransaction(
        String transactionId,
        String accountNumber,
        String cardNumber,
        BigDecimal amount,
        String currencyCode,
        String merchantId,
        String merchantName,
        LocalDate transactionDate,
        LocalTime transactionTime,
        String countryCode) {
}
