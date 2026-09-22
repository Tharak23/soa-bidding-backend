package com.bidding.biddingservice.client;

import java.util.UUID;

public record LeadingBidRequest(String bidderClerkUserId, long amountCents, UUID bidId) {
}
