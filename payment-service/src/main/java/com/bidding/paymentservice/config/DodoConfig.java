package com.bidding.paymentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dodopayments.api.client.DodoPaymentsClient;
import com.dodopayments.api.client.okhttp.DodoPaymentsOkHttpClient;

import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class DodoConfig {

	@Bean
	DodoPaymentsClient dodoPaymentsClient() {
		return DodoPaymentsOkHttpClient.fromEnv();
	}
}
