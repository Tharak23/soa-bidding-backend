package com.bidding.paymentservice.web;

import java.util.UUID;

public record CheckoutResponse(UUID paymentId, String checkoutUrl, String sessionId) {
}
