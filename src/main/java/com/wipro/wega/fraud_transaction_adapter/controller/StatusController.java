package com.wipro.wega.fraud_transaction_adapter.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wipro.wega.fraud_transaction_adapter.service.KafkaStatusService;

@RestController
public class StatusController {

    private final KafkaStatusService kafkaStatusService;

    public StatusController(KafkaStatusService kafkaStatusService) {
        this.kafkaStatusService = kafkaStatusService;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus() {
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "kafka", kafkaStatusService.status()));
    }
}
