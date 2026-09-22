package com.bidding.auctionservice.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class CloseDecisionTest {

	@Test
	void unsoldWhenNobodyBid() {
		CloseOutcome outcome = CloseDecision.decide(null, 1000);
		assertEquals("CLOSED", outcome.status());
		assertNull(outcome.winnerId());
		assertEquals(0, outcome.amountCents());
	}

	@Test
	void soldToLeadingBidder() {
		CloseOutcome outcome = CloseDecision.decide("winner", 4200);
		assertEquals("SOLD", outcome.status());
		assertEquals("winner", outcome.winnerId());
		assertEquals(4200, outcome.amountCents());
	}
}
