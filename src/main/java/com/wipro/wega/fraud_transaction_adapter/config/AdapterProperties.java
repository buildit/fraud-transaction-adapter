package com.wipro.wega.fraud_transaction_adapter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalised configuration for the adapter: Kafka topics and the downstream
 * credit-score service location. Bound from the {@code adapter.*} namespace.
 */
@ConfigurationProperties(prefix = "adapter")
public record AdapterProperties(KafkaTopics kafka, CreditScore creditScore) {

    public record KafkaTopics(String inboundTopic, String outboundTopic) {
    }

    public record CreditScore(String baseUrl, String scorePath) {
    }
}
