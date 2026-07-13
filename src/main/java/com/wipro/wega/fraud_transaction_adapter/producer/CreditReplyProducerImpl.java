package com.wipro.wega.fraud_transaction_adapter.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.wipro.wega.fraud_transaction_adapter.config.AdapterProperties;
import com.wipro.wega.fraud_transaction_adapter.logging.Loggable;
import com.wipro.wega.fraud_transaction_adapter.model.CreditReply;

/**
 * Sends {@link CreditReply} messages to the outbound topic, keyed by
 * transaction id so replies for the same transaction stay ordered.
 */
@Component
public class CreditReplyProducerImpl implements CreditReplyProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String outboundTopic;

    public CreditReplyProducerImpl(KafkaTemplate<String, Object> kafkaTemplate,
                                   AdapterProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.outboundTopic = properties.kafka().outboundTopic();
    }

    @Override
    @Loggable
    public void publish(CreditReply reply) {
        try {
            kafkaTemplate.send(outboundTopic, reply.transactionId(), reply).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while publishing to Kafka", e);
        } catch (java.util.concurrent.ExecutionException e) {
            throw new RuntimeException("Failed to publish to Kafka", e.getCause());
        }
    }
}
