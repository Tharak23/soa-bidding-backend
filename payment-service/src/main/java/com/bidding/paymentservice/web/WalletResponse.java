package com.bidding.paymentservice.web;

public record WalletResponse(long availableBalanceCents, long heldBalanceCents, String currency) {
}
