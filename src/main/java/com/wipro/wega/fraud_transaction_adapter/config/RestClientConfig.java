package com.wipro.wega.fraud_transaction_adapter.config;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Provides the {@link RestClient} used to call the downstream credit-score
 * service. The base URL is sourced from {@link AdapterProperties}.
 */
@Configuration
public class RestClientConfig {

    @Bean
    RestClient creditScoreRestClient(RestClient.Builder builder, AdapterProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(10));
        return builder
                .baseUrl(properties.creditScore().baseUrl())
                .requestFactory(factory)
                .build();
    }
}
