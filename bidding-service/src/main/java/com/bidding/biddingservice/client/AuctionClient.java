package com.bidding.biddingservice.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.bidding.common.feign.FeignConfig;

@FeignClient(name = "auction-service", configuration = FeignConfig.class)
public interface AuctionClient {

	@PostMapping("/internal/auctions/{id}/leading-bid")
	LeadingBidResponse leadingBid(@PathVariable("id") UUID id, @RequestBody LeadingBidRequest request);
}
