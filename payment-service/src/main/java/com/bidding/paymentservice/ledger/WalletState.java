package com.bidding.paymentservice.ledger;

public record WalletState(long availableCents, long heldCents) {
}
