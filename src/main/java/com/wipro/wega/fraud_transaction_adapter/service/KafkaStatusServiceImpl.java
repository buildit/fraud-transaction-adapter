package com.wipro.wega.fraud_transaction_adapter.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KafkaStatusServiceImpl implements KafkaStatusService {

    private final String bootstrapServers;

    public KafkaStatusServiceImpl(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    @Override
    public String status() {
        try (AdminClient adminClient = AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers))) {
            adminClient.describeCluster().nodes().get(3, TimeUnit.SECONDS);
            return "UP";
        } catch (Exception exception) {
            return "DOWN";
        }
    }
}