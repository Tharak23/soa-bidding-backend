package com.bidding.auctionservice.web;

import java.time.Instant;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAuctionRequest(
		@NotBlank @Size(max = 120) String title,
		@Size(max = 4000) String description,
		@Size(max = 80) String category,
		@Size(max = 40) String condition,
		@Size(max = 80) String location,
		@Size(max = 500) String imageUrl,
		@Min(0) long startPriceCents,
		@Min(1) long minIncrementCents,
		@NotNull @Future Instant endsAt) {
}
