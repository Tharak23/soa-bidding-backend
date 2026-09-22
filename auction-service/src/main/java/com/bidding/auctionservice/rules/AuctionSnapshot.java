package com.bidding.auctionservice.rules;

import java.time.Instant;

import com.bidding.auctionservice.domain.AuctionStatus;

public record AuctionSnapshot(
		AuctionStatus status,
		Instant endsAt,
		String sellerId,
		String leadingBidderId,
		long startPriceCents,
		long currentPriceCents,
		long minIncrementCents) {
}
