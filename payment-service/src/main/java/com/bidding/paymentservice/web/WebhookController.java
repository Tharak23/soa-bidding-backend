package com.bidding.paymentservice.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidding.paymentservice.service.WalletService;

@RestController
@RequestMapping("/api/payments")
public class WebhookController {

	private final WalletService wallets;

	public WebhookController(WalletService wallets) {
		this.wallets = wallets;
	}

	@PostMapping("/webhook")
	public ResponseEntity<Void> webhook(
			@RequestBody String rawBody,
			@RequestHeader(value = "webhook-id", required = false) String webhookId,
			@RequestHeader(value = "webhook-signature", required = false) String signature,
			@RequestHeader(value = "webhook-timestamp", required = false) String timestamp) {
		wallets.handleWebhook(rawBody, webhookId, signature, timestamp);
		return ResponseEntity.ok().build();
	}
}
