package com.bidding.biddingservice.client;

import java.util.UUID;

public record HoldRequest(String clerkUserId, long amountCents, UUID auctionId, UUID bidId, String idempotencyKey) {
}
