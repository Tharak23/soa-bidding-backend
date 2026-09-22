package com.bidding.auctionservice.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bids")
public class Bid {

	@Id
	private UUID id;

	@Column(name = "auction_id", nullable = false)
	private UUID auctionId;

	@Column(name = "bidder_clerk_user_id", nullable = false)
	private String bidderClerkUserId;

	@Column(name = "amount_cents", nullable = false)
	private long amountCents;

	private String status;

	@Column(name = "created_at")
	private Instant createdAt;

	public UUID getId() {
		return id;
	}

	public UUID getAuctionId() {
		return auctionId;
	}

	public String getBidderClerkUserId() {
		return bidderClerkUserId;
	}

	public long getAmountCents() {
		return amountCents;
	}

	public String getStatus() {
		return status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
