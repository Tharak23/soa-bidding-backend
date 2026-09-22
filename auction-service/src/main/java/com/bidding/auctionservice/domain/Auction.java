package com.bidding.auctionservice.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "auctions")
public class Auction {

	@Id
	private UUID id;

	@Column(name = "seller_clerk_user_id", nullable = false)
	private String sellerClerkUserId;

	@Column(nullable = false)
	private String title;

	private String description;

	@Column(name = "image_url")
	private String imageUrl;

	private String category;

	@Column(name = "condition")
	private String condition;

	private String location;

	@Column(name = "start_price_cents", nullable = false)
	private long startPriceCents;

	@Column(name = "min_increment_cents", nullable = false)
	private long minIncrementCents;

	@Column(name = "current_price_cents", nullable = false)
	private long currentPriceCents;

	@Column(name = "leading_bidder_clerk_user_id")
	private String leadingBidderClerkUserId;

	@Column(name = "leading_bid_id")
	private UUID leadingBidId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AuctionStatus status = AuctionStatus.OPEN;

	@Column(name = "ends_at", nullable = false)
	private Instant endsAt;

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

	public void setId(UUID id) {
		this.id = id;
	}

	public String getSellerClerkUserId() {
		return sellerClerkUserId;
	}

	public void setSellerClerkUserId(String sellerClerkUserId) {
		this.sellerClerkUserId = sellerClerkUserId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public long getStartPriceCents() {
		return startPriceCents;
	}

	public void setStartPriceCents(long startPriceCents) {
		this.startPriceCents = startPriceCents;
	}

	public long getMinIncrementCents() {
		return minIncrementCents;
	}

	public void setMinIncrementCents(long minIncrementCents) {
		this.minIncrementCents = minIncrementCents;
	}

	public long getCurrentPriceCents() {
		return currentPriceCents;
	}

	public void setCurrentPriceCents(long currentPriceCents) {
		this.currentPriceCents = currentPriceCents;
	}

	public String getLeadingBidderClerkUserId() {
		return leadingBidderClerkUserId;
	}

	public void setLeadingBidderClerkUserId(String leadingBidderClerkUserId) {
		this.leadingBidderClerkUserId = leadingBidderClerkUserId;
	}

	public UUID getLeadingBidId() {
		return leadingBidId;
	}

	public void setLeadingBidId(UUID leadingBidId) {
		this.leadingBidId = leadingBidId;
	}

	public AuctionStatus getStatus() {
		return status;
	}

	public void setStatus(AuctionStatus status) {
		this.status = status;
	}

	public Instant getEndsAt() {
		return endsAt;
	}

	public void setEndsAt(Instant endsAt) {
		this.endsAt = endsAt;
	}

	public long getVersion() {
		return version;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
