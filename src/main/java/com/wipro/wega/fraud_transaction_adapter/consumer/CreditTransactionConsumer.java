package com.wipro.wega.fraud_transaction_adapter.consumer;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.wipro.wega.fraud_transaction_adapter.logging.Loggable;
import com.wipro.wega.fraud_transaction_adapter.service.CreditTransactionService;

/**
 * Entry point of the journey: consumes raw COBOL fixed-width records from the
 * inbound topic and seeds the MDC correlation id before delegating to the
 * service.
 */
@Component
public class CreditTransactionConsumer {

    private static final String MDC_CORRELATION_ID = "correlationId";

    private final CreditTransactionService creditTransactionService;

    public CreditTransactionConsumer(CreditTransactionService creditTransactionService) {
        this.creditTransactionService = creditTransactionService;
    }

    @KafkaListener(topics = "${adapter.kafka.inbound-topic}")
    @Loggable
    public void onMessage(String record) {
        java.util.Map<String, String> previousContext = MDC.getCopyOfContextMap();
        MDC.put(MDC_CORRELATION_ID, UUID.randomUUID().toString());
        try {
            creditTransactionService.process(record);
        } finally {
            if (previousContext != null) {
                MDC.setContextMap(previousContext);
            } else {
                MDC.clear();
            }
        }
    }
}
