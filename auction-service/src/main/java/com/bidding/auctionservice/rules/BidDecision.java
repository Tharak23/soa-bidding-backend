package com.bidding.auctionservice.rules;

public record BidDecision(boolean accepted, String code, String message, long minimumCents) {

	public static BidDecision ok(long minimumCents) {
		return new BidDecision(true, "ok", "accepted", minimumCents);
	}

	public static BidDecision reject(String code, String message, long minimumCents) {
		return new BidDecision(false, code, message, minimumCents);
	}
}
