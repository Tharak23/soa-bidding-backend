package com.bidding.paymentservice.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bidding.paymentservice.domain.WebhookEvent;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, UUID> {

	Optional<WebhookEvent> findByWebhookId(String webhookId);
}
