package com.bidding.auctionservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bidding.common.feign.FeignConfig;

@FeignClient(name = "payment-service", configuration = FeignConfig.class)
public interface PaymentClient {

	@PostMapping("/internal/wallet/settle")
	void settle(@RequestBody SettleRequest request);
}
