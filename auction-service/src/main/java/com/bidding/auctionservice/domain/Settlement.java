package com.bidding.auctionservice.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "settlements")
public class Settlement {

	@Id
	private UUID id;

	@Column(name = "auction_id", nullable = false, unique = true)
	private UUID auctionId;

	@Column(name = "winner_clerk_user_id")
	private String winnerClerkUserId;

	@Column(name = "amount_cents", nullable = false)
	private long amountCents;

	@Column(name = "captured_at")
	private Instant capturedAt;

	@PrePersist
	void onCreate() {
		if (id == null) {
			id = UUID.randomUUID();
		}
		if (capturedAt == null) {
			capturedAt = Instant.now();
		}
	}

	public void setAuctionId(UUID auctionId) {
		this.auctionId = auctionId;
	}

	public void setWinnerClerkUserId(String winnerClerkUserId) {
		this.winnerClerkUserId = winnerClerkUserId;
	}

	public void setAmountCents(long amountCents) {
		this.amountCents = amountCents;
	}

	public UUID getAuctionId() {
		return auctionId;
	}

	public String getWinnerClerkUserId() {
		return winnerClerkUserId;
	}

	public long getAmountCents() {
		return amountCents;
	}
}
