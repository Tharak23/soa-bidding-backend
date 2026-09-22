package com.bidding.paymentservice.web;

import java.util.UUID;

public record ReleaseRequest(String clerkUserId, UUID auctionId, UUID bidId, String idempotencyKey) {
}
