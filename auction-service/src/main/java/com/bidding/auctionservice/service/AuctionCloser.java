package com.bidding.auctionservice.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AuctionCloser {

	private final AuctionService auctions;

	public AuctionCloser(AuctionService auctions) {
		this.auctions = auctions;
	}

	@Scheduled(fixedDelay = 1000)
	public void tick() {
		auctions.closeDueAuctions();
	}
}
