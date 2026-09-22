package com.bidding.auctionservice.rules;

public final class CloseDecision {

	private CloseDecision() {
	}

	public static CloseOutcome decide(String leadingBidderId, long currentPriceCents) {
		if (leadingBidderId == null || leadingBidderId.isBlank()) {
			return new CloseOutcome("CLOSED", null, 0);
		}
		return new CloseOutcome("SOLD", leadingBidderId, currentPriceCents);
	}
}
