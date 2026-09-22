package com.bidding.auctionservice.web;

import java.time.Instant;
import java.util.UUID;

import com.bidding.auctionservice.domain.Auction;
import com.bidding.auctionservice.domain.AuctionStatus;

public record AuctionResponse(
		UUID id,
		String sellerClerkUserId,
		String title,
		String description,
		String category,
		String condition,
		String location,
		String imageUrl,
		long startPriceCents,
		long minIncrementCents,
		long currentPriceCents,
		String leadingBidderClerkUserId,
		UUID leadingBidId,
		AuctionStatus status,
		Instant endsAt,
		Instant createdAt) {

	public static AuctionResponse from(Auction auction) {
		return new AuctionResponse(
				auction.getId(),
				auction.getSellerClerkUserId(),
				auction.getTitle(),
				auction.getDescription(),
				auction.getCategory(),
				auction.getCondition(),
				auction.getLocation(),
				auction.getImageUrl(),
				auction.getStartPriceCents(),
				auction.getMinIncrementCents(),
				auction.getCurrentPriceCents(),
				auction.getLeadingBidderClerkUserId(),
				auction.getLeadingBidId(),
				auction.getStatus(),
				auction.getEndsAt(),
				auction.getCreatedAt());
	}
}
