package com.bidding.biddingservice.web;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PlaceBidRequest(@NotNull UUID auctionId, @Min(1) long amountCents) {
}
