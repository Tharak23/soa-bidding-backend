package com.bidding.auctionservice.web;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LeadingBidRequest(
		@NotBlank String bidderClerkUserId,
		@Min(1) long amountCents,
		@NotNull UUID bidId) {
}
