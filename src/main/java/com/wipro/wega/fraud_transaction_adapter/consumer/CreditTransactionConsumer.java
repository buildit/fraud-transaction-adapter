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
        String previousCorrelationId = MDC.get(MDC_CORRELATION_ID);
        MDC.put(MDC_CORRELATION_ID, UUID.randomUUID().toString());
        try {
            // WARNING: Asynchronous processing without manual acknowledgment leads to message loss on failure.
            // Consider changing the service to process synchronously or use manual Kafka Acknowledgment.
            creditTransactionService.process(record);
        } finally {
            if (previousCorrelationId != null) {
                MDC.put(MDC_CORRELATION_ID, previousCorrelationId);
            } else {
                MDC.remove(MDC_CORRELATION_ID);
            }
        }
    }
}
