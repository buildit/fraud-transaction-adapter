package com.wipro.wega.fraud_transaction_adapter.service;

/**
 * Orchestrates a single credit-transaction journey: parse the COBOL record,
 * obtain a credit score, and publish the reply.
 */
public interface CreditTransactionService {

    /**
     * Processes one inbound fixed-width credit transaction record.
     *
     * @param rawRecord the COBOL fixed-width message payload
     * @return a future completing when the processing is finished
     */
    java.util.concurrent.CompletableFuture<Void> process(String rawRecord);
}
