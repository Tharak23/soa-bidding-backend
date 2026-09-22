package com.bidding.authservice.web;

public record ProfileResponse(
		java.util.UUID id,
		String clerkUserId,
		String email,
		String displayName,
		String avatarUrl,
		boolean onboarded) {
}
