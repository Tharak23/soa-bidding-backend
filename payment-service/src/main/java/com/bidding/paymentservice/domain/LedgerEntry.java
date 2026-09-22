package com.bidding.paymentservice.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {

	@Id
	private UUID id;

	@Column(name = "profile_id", nullable = false)
	private UUID profileId;

	@Column(name = "clerk_user_id", nullable = false)
	private String clerkUserId;

	@Column(name = "entry_type", nullable = false)
	private String entryType;

	@Column(name = "amount_cents", nullable = false)
	private long amountCents;

	@Column(name = "auction_id")
	private UUID auctionId;

	@Column(name = "bid_id")
	private UUID bidId;

	@Column(name = "dodo_payment_id")
	private String dodoPaymentId;

	@Column(name = "idempotency_key", nullable = false, unique = true)
	private String idempotencyKey;

	@Column(name = "created_at")
	private Instant createdAt;

	@PrePersist
	void onCreate() {
		if (id == null) {
			id = UUID.randomUUID();
		}
		createdAt = Instant.now();
	}

	public static LedgerEntry of(UUID profileId, String clerkUserId, String type, long amount, UUID auctionId, UUID bidId, String dodoPaymentId, String idempotencyKey) {
		LedgerEntry entry = new LedgerEntry();
		entry.profileId = profileId;
		entry.clerkUserId = clerkUserId;
		entry.entryType = type;
		entry.amountCents = amount;
		entry.auctionId = auctionId;
		entry.bidId = bidId;
		entry.dodoPaymentId = dodoPaymentId;
		entry.idempotencyKey = idempotencyKey;
		return entry;
	}

	public UUID getId() {
		return id;
	}

	public UUID getProfileId() {
		return profileId;
	}

	public String getClerkUserId() {
		return clerkUserId;
	}

	public String getEntryType() {
		return entryType;
	}

	public long getAmountCents() {
		return amountCents;
	}

	public UUID getAuctionId() {
		return auctionId;
	}

	public UUID getBidId() {
		return bidId;
	}

	public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
