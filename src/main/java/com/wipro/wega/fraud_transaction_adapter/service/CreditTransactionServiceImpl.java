package com.wipro.wega.fraud_transaction_adapter.service;

import java.time.Instant;
import java.util.Map;

import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import com.wipro.wega.fraud_transaction_adapter.client.CreditScoreClient;
import com.wipro.wega.fraud_transaction_adapter.logging.Loggable;
import com.wipro.wega.fraud_transaction_adapter.model.CreditReply;
import com.wipro.wega.fraud_transaction_adapter.model.CreditScoreResponse;
import com.wipro.wega.fraud_transaction_adapter.model.CreditTransaction;
import com.wipro.wega.fraud_transaction_adapter.producer.CreditReplyProducer;
import com.wipro.wega.fraud_transaction_adapter.transformer.CobolTransactionTransformer;

/**
 * Default journey orchestration. Transformation runs on the listener thread;
 * scoring is dispatched asynchronously and the reply is published from the
 * completion callback once the downstream response arrives.
 */
@Service
public class CreditTransactionServiceImpl implements CreditTransactionService {

    private static final String MDC_TRANSACTION_ID = "transactionId";

    private final CobolTransactionTransformer transformer;
    private final CreditScoreClient creditScoreClient;
    private final CreditReplyProducer creditReplyProducer;

    public CreditTransactionServiceImpl(CobolTransactionTransformer transformer,
                                        CreditScoreClient creditScoreClient,
                                        CreditReplyProducer creditReplyProducer) {
        this.transformer = transformer;
        this.creditScoreClient = creditScoreClient;
        this.creditReplyProducer = creditReplyProducer;
    }

    @Override
    @Loggable
    public void process(String rawRecord) {
        CreditTransaction transaction = transformer.transform(rawRecord);
        String previousTransactionId = MDC.get(MDC_TRANSACTION_ID);
        MDC.put(MDC_TRANSACTION_ID, transaction.transactionId());

        try {
            // Capture the journey context so the async completion callback logs
            // under the same correlation/transaction ids as the listener thread.
            Map<String, String> journeyContext = MDC.getCopyOfContextMap();

            creditScoreClient.requestScore(transaction)
                    .thenAccept(response -> publishReply(transaction, response, journeyContext))
                    .exceptionally(ex -> {
                        handleFailure(transaction, ex, journeyContext);
                        return null;
                    });
        } finally {
            if (previousTransactionId != null) {
                MDC.put(MDC_TRANSACTION_ID, previousTransactionId);
            } else {
                MDC.remove(MDC_TRANSACTION_ID);
            }
        }
    }

    private void publishReply(CreditTransaction transaction,
                              CreditScoreResponse response,
                              Map<String, String> journeyContext) {
        withContext(journeyContext, () -> {
            CreditReply reply = new CreditReply(
                    response.transactionId(),
                    transaction.accountNumber(),
                    response.score(),
                    response.decision(),
                    response.reason(),
                    Instant.now());
            creditReplyProducer.publish(reply);
        });
    }

    private void handleFailure(CreditTransaction transaction, Throwable ex,
                               Map<String, String> journeyContext) {
        withContext(journeyContext, () ->
                org.slf4j.LoggerFactory.getLogger(CreditTransactionServiceImpl.class)
                        .error("Credit scoring failed for transactionId={}",
                                transaction.transactionId(), ex));
    }

    private void withContext(Map<String, String> context, Runnable action) {
        if (context != null) {
            MDC.setContextMap(context);
        }
        try {
            action.run();
        } finally {
            MDC.clear();
        }
    }
}
