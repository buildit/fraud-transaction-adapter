package com.wipro.wega.fraud_transaction_adapter.client;

import java.util.concurrent.CompletableFuture;

import com.wipro.wega.fraud_transaction_adapter.model.CreditScoreResponse;
import com.wipro.wega.fraud_transaction_adapter.model.CreditTransaction;

/**
 * Client for the downstream credit-score service. Per enterprise guidelines,
 * every API-calling service method is fronted by an interface.
 */
public interface CreditScoreClient {

    /**
     * Requests a credit score for the given transaction without blocking the
     * caller's thread.
     *
     * @param transaction the transaction to score
     * @return a future completing with the credit-score response
     */
    CompletableFuture<CreditScoreResponse> requestScore(CreditTransaction transaction);
}
