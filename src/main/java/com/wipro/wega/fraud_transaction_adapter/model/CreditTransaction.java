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

    @Override
    public String toString() {
        String maskedCard = (cardNumber != null && cardNumber.length() >= 4) 
                ? "********" + cardNumber.substring(cardNumber.length() - 4) 
                : "****";
        return "CreditTransaction[" +
                "transactionId=" + transactionId + ", " +
                "accountNumber=" + accountNumber + ", " +
                "cardNumber=" + maskedCard + ", " +
                "amount=" + amount + ", " +
                "currencyCode=" + currencyCode + ", " +
                "merchantId=" + merchantId + ", " +
                "merchantName=" + merchantName + ", " +
                "transactionDate=" + transactionDate + ", " +
                "transactionTime=" + transactionTime + ", " +
                "countryCode=" + countryCode + ']';
    }
}
