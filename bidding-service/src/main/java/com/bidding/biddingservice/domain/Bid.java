package com.bidding.biddingservice.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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

	@Column(nullable = false)
	private String status = "accepted";

	@Column(name = "created_at")
	private Instant createdAt;

	@PrePersist
	void onCreate() {
		if (id == null) {
			id = UUID.randomUUID();
		}
		createdAt = Instant.now();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getAuctionId() {
		return auctionId;
	}

	public void setAuctionId(UUID auctionId) {
		this.auctionId = auctionId;
	}

	public String getBidderClerkUserId() {
		return bidderClerkUserId;
	}

	public void setBidderClerkUserId(String bidderClerkUserId) {
		this.bidderClerkUserId = bidderClerkUserId;
	}

	public long getAmountCents() {
		return amountCents;
	}

	public void setAmountCents(long amountCents) {
		this.amountCents = amountCents;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
