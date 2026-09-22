package com.bidding.biddingservice.client;

import java.util.UUID;

public record ReleaseRequest(String clerkUserId, UUID auctionId, UUID bidId, String idempotencyKey) {
}
