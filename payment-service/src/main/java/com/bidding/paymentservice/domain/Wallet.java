package com.bidding.paymentservice.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "wallets")
public class Wallet {

	@Id
	private UUID id;

	@Column(name = "profile_id", nullable = false, unique = true)
	private UUID profileId;

	@Column(name = "clerk_user_id", nullable = false, unique = true)
	private String clerkUserId;

	@Column(name = "available_balance_cents", nullable = false)
	private long availableBalanceCents;

	@Column(name = "held_balance_cents", nullable = false)
	private long heldBalanceCents;

	@Column(nullable = false)
	private String currency = "INR";

	@Version
	private long version;

	@Column(name = "created_at")
	private Instant createdAt;

	@Column(name = "updated_at")
	private Instant updatedAt;

	@PrePersist
	void onCreate() {
		if (id == null) {
			id = UUID.randomUUID();
		}
		createdAt = Instant.now();
		updatedAt = createdAt;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = Instant.now();
	}

	public UUID getId() {
		return id;
	}

	public UUID getProfileId() {
		return profileId;
	}

	public void setProfileId(UUID profileId) {
		this.profileId = profileId;
	}

	public String getClerkUserId() {
		return clerkUserId;
	}

	public void setClerkUserId(String clerkUserId) {
		this.clerkUserId = clerkUserId;
	}

	public long getAvailableBalanceCents() {
		return availableBalanceCents;
	}

	public void setAvailableBalanceCents(long availableBalanceCents) {
		this.availableBalanceCents = availableBalanceCents;
	}

	public long getHeldBalanceCents() {
		return heldBalanceCents;
	}

	public void setHeldBalanceCents(long heldBalanceCents) {
		this.heldBalanceCents = heldBalanceCents;
	}

	public String getCurrency() {
		return currency;
	}
}
