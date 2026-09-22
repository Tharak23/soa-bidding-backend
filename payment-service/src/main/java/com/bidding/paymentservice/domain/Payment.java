package com.bidding.paymentservice.domain;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments")
public class Payment {

	@Id
	private UUID id;

	@Column(name = "profile_id", nullable = false)
	private UUID profileId;

	@Column(name = "clerk_user_id", nullable = false)
	private String clerkUserId;

	@Column(name = "dodo_payment_id")
	private String dodoPaymentId;

	@Column(name = "dodo_checkout_session_id")
	private String dodoCheckoutSessionId;

	@Column(name = "product_id", nullable = false)
	private String productId = "pdt_0No8GYiVeUpU21JfBYefp";

	@Column(name = "amount_cents", nullable = false)
	private long amountCents;

	@Column(nullable = false)
	private String currency = "INR";

	@Column(nullable = false)
	private String status = "open";

	@Column(nullable = false)
	private String purpose = "wallet_top_up";

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private String metadata = "{}";

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

	public String getDodoPaymentId() {
		return dodoPaymentId;
	}

	public void setDodoPaymentId(String dodoPaymentId) {
		this.dodoPaymentId = dodoPaymentId;
	}

	public String getDodoCheckoutSessionId() {
		return dodoCheckoutSessionId;
	}

	public void setDodoCheckoutSessionId(String dodoCheckoutSessionId) {
		this.dodoCheckoutSessionId = dodoCheckoutSessionId;
	}

	public String getProductId() {
		return productId;
	}

	public long getAmountCents() {
		return amountCents;
	}

	public void setAmountCents(long amountCents) {
		this.amountCents = amountCents;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
}
