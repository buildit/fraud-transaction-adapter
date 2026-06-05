package com.wipro.wega.fraud_transaction_adapter.client;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.wipro.wega.fraud_transaction_adapter.config.AdapterProperties;
import com.wipro.wega.fraud_transaction_adapter.logging.Loggable;
import com.wipro.wega.fraud_transaction_adapter.model.CreditScoreResponse;
import com.wipro.wega.fraud_transaction_adapter.model.CreditTransaction;

/**
 * Calls {@code POST {base-url}/credit/score} on a managed async executor so the
 * Kafka listener thread is never blocked waiting on the downstream service.
 */
@Component
public class CreditScoreClientImpl implements CreditScoreClient {

    private final RestClient restClient;
    private final String scorePath;

    public CreditScoreClientImpl(RestClient creditScoreRestClient, AdapterProperties properties) {
        this.restClient = creditScoreRestClient;
        this.scorePath = properties.creditScore().scorePath();
    }

    @Override
    @Async("creditScoreExecutor")
    @Loggable
    public CompletableFuture<CreditScoreResponse> requestScore(CreditTransaction transaction) {
        CreditScoreResponse response = restClient.post()
                .uri(scorePath)
                .body(transaction)
                .retrieve()
                .body(CreditScoreResponse.class);
        return CompletableFuture.completedFuture(response);
    }
}
