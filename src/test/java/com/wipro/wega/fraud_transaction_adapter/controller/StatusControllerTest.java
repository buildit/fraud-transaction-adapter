package com.wipro.wega.fraud_transaction_adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.wipro.wega.fraud_transaction_adapter.service.KafkaStatusService;

@ExtendWith(MockitoExtension.class)
class StatusControllerTest {

    @Mock
    private KafkaStatusService kafkaStatusService;

    @Test
    void returnsServiceAndKafkaStatus() {
        when(kafkaStatusService.status()).thenReturn("UP");

        StatusController controller = new StatusController(kafkaStatusService);
        ResponseEntity<Map<String, String>> response = controller.getStatus();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsEntry("status", "OK")
                .containsEntry("kafka", "UP");
    }
}