package com.bidding.auctionservice.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.bidding.auctionservice.domain.AuctionStatus;

class BidRulesTest {

	private final Instant now = Instant.parse("2026-01-01T12:00:00Z");
	private final Instant ends = Instant.parse("2026-01-01T13:00:00Z");

	@Test
	void firstBidMustMeetStartPrice() {
		AuctionSnapshot auction = open(null, 5000, 5000);
		BidDecision low = BidRules.evaluate(auction, "bidder", 4999, now);
		assertFalse(low.accepted());
		assertEquals("bid_too_low", low.code());
		assertTrue(BidRules.evaluate(auction, "bidder", 5000, now).accepted());
	}

	@Test
	void laterBidMustBeatIncrement() {
		AuctionSnapshot auction = open("leader", 5000, 5500);
		assertFalse(BidRules.evaluate(auction, "bidder", 5599, now).accepted());
		assertTrue(BidRules.evaluate(auction, "bidder", 5600, now).accepted());
	}

	@Test
	void sellerCannotBid() {
		assertEquals("seller_cannot_bid", BidRules.evaluate(open(null, 100, 100), "seller", 100, now).code());
	}

	@Test
	void rejectsAfterEnd() {
		assertEquals("auction_closed", BidRules.evaluate(open(null, 100, 100), "bidder", 100, ends).code());
	}

	@Test
	void rejectsWhenNotOpen() {
		AuctionSnapshot closed = new AuctionSnapshot(AuctionStatus.SOLD, ends, "seller", "leader", 100, 200, 100);
		assertEquals("auction_not_open", BidRules.evaluate(closed, "bidder", 400, now).code());
	}

	private AuctionSnapshot open(String leader, long start, long current) {
		return new AuctionSnapshot(AuctionStatus.OPEN, ends, "seller", leader, start, current, 100);
	}
}
