package com.bidding.paymentservice.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "profiles")
public class Profile {

	@Id
	private UUID id;

	@Column(name = "clerk_user_id", nullable = false, unique = true)
	private String clerkUserId;

	private String email;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getClerkUserId() {
		return clerkUserId;
	}

	public void setClerkUserId(String clerkUserId) {
		this.clerkUserId = clerkUserId;
	}

	public String getEmail() {
		return email;
	}
}
