package com.wipro.wega.fraud_transaction_adapter.model;

/**
 * Response returned by the downstream credit-score service for a transaction.
 */
public record CreditScoreResponse(
        String transactionId,
        int score,
        String decision,
        String reason) {
}
