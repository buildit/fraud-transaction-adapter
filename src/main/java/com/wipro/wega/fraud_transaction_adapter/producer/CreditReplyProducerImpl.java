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
        kafkaTemplate.send(outboundTopic, reply.transactionId(), reply)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        org.slf4j.LoggerFactory.getLogger(CreditReplyProducerImpl.class)
                                .error("Failed to publish reply for transactionId={}", reply.transactionId(), ex);
                    }
                });
    }
}
