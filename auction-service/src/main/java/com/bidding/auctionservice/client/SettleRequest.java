package com.bidding.auctionservice.client;

import java.util.UUID;

public record SettleRequest(UUID auctionId, String winnerClerkUserId, UUID winningBidId, long amountCents) {
}
