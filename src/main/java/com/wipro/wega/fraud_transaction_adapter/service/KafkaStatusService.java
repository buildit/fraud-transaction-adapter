package com.wipro.wega.fraud_transaction_adapter.service;

/**
 * Reports the adapter's connectivity to the Kafka cluster. Per enterprise
 * guidelines, the broker call is fronted by an interface.
 */
public interface KafkaStatusService {

    /**
     * Probes the Kafka cluster and reports whether it is reachable.
     *
     * @return {@code "UP"} when the broker responds, {@code "DOWN"} otherwise
     */
    String status();
}
