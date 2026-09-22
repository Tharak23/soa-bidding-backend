package com.bidding.authservice.web;

import jakarta.validation.constraints.Size;

public record PatchProfileRequest(
		@Size(max = 80) String displayName,
		String avatarUrl,
		Boolean onboarded) {
}
