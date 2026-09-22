package com.bidding.auctionservice.rules;

public record CloseOutcome(String status, String winnerId, long amountCents) {
}
