package com.wipro.wega.fraud_transaction_adapter.config;

import java.time.Duration;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Provides the {@link RestClient} used to call the downstream credit-score
 * service. The base URL is sourced from {@link AdapterProperties}.
 */
@Configuration
public class RestClientConfig {

    @Bean
    RestClient creditScoreRestClient(RestClient.Builder builder, AdapterProperties properties) {
        return builder
                .baseUrl(properties.creditScore().baseUrl())
                .requestFactory(ClientHttpRequestFactories.get(
                        ClientHttpRequestFactorySettings.DEFAULTS
                                .withConnectTimeout(Duration.ofSeconds(5))
                                .withReadTimeout(Duration.ofSeconds(5))))
                .build();
    }
}
