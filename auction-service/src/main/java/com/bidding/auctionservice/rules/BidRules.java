package com.bidding.auctionservice.rules;

import java.time.Instant;

import com.bidding.auctionservice.domain.AuctionStatus;

public final class BidRules {

	private BidRules() {
	}

	public static BidDecision evaluate(AuctionSnapshot auction, String bidderId, long amountCents, Instant now) {
		long minimum = minimumBid(auction);
		if (auction.status() != AuctionStatus.OPEN) {
			return BidDecision.reject("auction_not_open", "Auction is not open for bids", minimum);
		}
		if (!now.isBefore(auction.endsAt())) {
			return BidDecision.reject("auction_closed", "Auction has ended", minimum);
		}
		if (auction.sellerId().equals(bidderId)) {
			return BidDecision.reject("seller_cannot_bid", "Sellers cannot bid on their own listing", minimum);
		}
		if (amountCents < minimum) {
			return BidDecision.reject("bid_too_low", "Bid must be at least " + minimum + " cents", minimum);
		}
		return BidDecision.ok(minimum);
	}

	public static long minimumBid(AuctionSnapshot auction) {
		if (auction.leadingBidderId() == null || auction.leadingBidderId().isBlank()) {
			return auction.startPriceCents();
		}
		return auction.currentPriceCents() + auction.minIncrementCents();
	}
}
