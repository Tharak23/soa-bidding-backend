package com.bidding.paymentservice.web;

import jakarta.validation.constraints.Min;

public record TopUpRequest(@Min(1) int amountCents) {
}
