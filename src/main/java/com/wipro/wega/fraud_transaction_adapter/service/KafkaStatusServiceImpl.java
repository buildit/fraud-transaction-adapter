package com.wipro.wega.fraud_transaction_adapter.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PreDestroy;

@Service
public class KafkaStatusServiceImpl implements KafkaStatusService {

    private final AdminClient adminClient;

    public KafkaStatusServiceImpl(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        this.adminClient = AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers));
    }

    @Override
    public String status() {
        try {
            adminClient.describeCluster().nodes().get(3, TimeUnit.SECONDS);
            return "UP";
        } catch (Exception exception) {
            return "DOWN";
        }
    }

    @PreDestroy
    public void close() {
        if (adminClient != null) {
            adminClient.close();
        }
    }
}