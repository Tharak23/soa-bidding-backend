package com.bidding.paymentservice.web;

import java.util.UUID;

public record SettleRequest(UUID auctionId, String winnerClerkUserId, UUID winningBidId, long amountCents) {
}
