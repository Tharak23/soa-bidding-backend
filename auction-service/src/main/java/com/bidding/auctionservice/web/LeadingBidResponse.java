package com.bidding.auctionservice.web;

import java.util.UUID;

public record LeadingBidResponse(boolean accepted, String code, String message, UUID previousBidId, String previousBidderId) {
}
