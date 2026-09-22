package com.bidding.paymentservice.web;

import java.util.UUID;

public record HoldRequest(String clerkUserId, long amountCents, UUID auctionId, UUID bidId, String idempotencyKey) {
}
