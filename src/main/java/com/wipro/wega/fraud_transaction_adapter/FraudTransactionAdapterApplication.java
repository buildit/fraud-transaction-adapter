package com.wipro.wega.fraud_transaction_adapter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FraudTransactionAdapterApplication {

	public static void main(String[] args) {
		SpringApplication.run(FraudTransactionAdapterApplication.class, args);
	}

}
