package com.bidding.paymentservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

import com.dodopayments.api.client.DodoPaymentsClient;
import com.dodopayments.api.client.okhttp.DodoPaymentsOkHttpClient;

@Configuration
@Profile("!test")
public class DodoConfig {

	@Value("${DODO_PAYMENTS_API_KEY:}")
	private String apiKey;

	@Value("${DODO_PAYMENTS_BASE_URL:https://test.dodopayments.com}")
	private String baseUrl;

	@Value("${DODO_PAYMENTS_WEBHOOK_KEY:}")
	private String webhookKey;

	@Bean
	DodoPaymentsClient dodoPaymentsClient() {
		var builder = DodoPaymentsOkHttpClient.builder().fromEnv();
		if (StringUtils.hasText(apiKey)) {
			builder.bearerToken(apiKey);
		}
		if (StringUtils.hasText(baseUrl)) {
			builder.baseUrl(baseUrl);
		}
		if (StringUtils.hasText(webhookKey)) {
			builder.webhookKey(webhookKey);
		}
		try {
			return builder.build();
		} catch (IllegalStateException ex) {
			return DodoPaymentsOkHttpClient.builder()
					.bearerToken("dev-placeholder-token")
					.baseUrl(baseUrl)
					.build();
		}
	}
}
