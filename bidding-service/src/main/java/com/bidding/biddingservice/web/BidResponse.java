package com.bidding.biddingservice.web;

import java.time.Instant;
import java.util.UUID;

import com.bidding.biddingservice.domain.Bid;

public record BidResponse(UUID id, UUID auctionId, String bidderClerkUserId, long amountCents, String status, Instant createdAt) {

	public static BidResponse from(Bid bid) {
		return new BidResponse(bid.getId(), bid.getAuctionId(), bid.getBidderClerkUserId(), bid.getAmountCents(), bid.getStatus(), bid.getCreatedAt());
	}
}
