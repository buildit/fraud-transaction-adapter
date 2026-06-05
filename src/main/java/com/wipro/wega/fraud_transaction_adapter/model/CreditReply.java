package com.wipro.wega.fraud_transaction_adapter.model;

import java.time.Instant;

/**
 * Reply published to the outbound topic once a credit score has been obtained.
 */
public record CreditReply(
        String transactionId,
        String accountNumber,
        int score,
        String decision,
        String reason,
        Instant scoredAt) {
}
