package com.wipro.wega.fraud_transaction_adapter.producer;

import com.wipro.wega.fraud_transaction_adapter.model.CreditReply;

/**
 * Publishes credit replies to the outbound Kafka topic. Fronted by an interface
 * per enterprise guidelines for API-calling service methods.
 */
public interface CreditReplyProducer {

    void publish(CreditReply reply);
}
