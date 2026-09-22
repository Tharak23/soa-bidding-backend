package com.bidding.authservice.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bidding.authservice.domain.Profile;
import com.bidding.authservice.repo.ProfileRepository;
import com.bidding.authservice.web.PatchProfileRequest;
import com.bidding.authservice.web.ProfileResponse;
import com.bidding.common.security.CurrentUser;

@Service
public class ProfileService {

	private final ProfileRepository profiles;

	public ProfileService(ProfileRepository profiles) {
		this.profiles = profiles;
	}

	@Transactional
	public ProfileResponse me() {
		Profile profile = upsert();
		return toResponse(profile);
	}

	@Transactional
	public ProfileResponse update(PatchProfileRequest request) {
		Profile profile = upsert();
		if (request.displayName() != null && !request.displayName().isBlank()) {
			profile.setDisplayName(request.displayName().trim());
		}
		if (request.avatarUrl() != null) {
			profile.setAvatarUrl(request.avatarUrl());
		}
		if (request.onboarded() != null) {
			profile.setOnboarded(request.onboarded());
		}
		return toResponse(profiles.save(profile));
	}

	private Profile upsert() {
		String clerkUserId = CurrentUser.clerkUserId();
		return profiles.findByClerkUserId(clerkUserId).orElseGet(() -> {
			Profile created = new Profile();
			created.setClerkUserId(clerkUserId);
			created.setEmail(CurrentUser.email());
			created.setDisplayName(CurrentUser.displayName());
			created.setOnboarded(false);
			try {
				return profiles.saveAndFlush(created);
			} catch (DataIntegrityViolationException duplicate) {
				return profiles.findByClerkUserId(clerkUserId).orElseThrow(() -> duplicate);
			}
		});
	}

	private static ProfileResponse toResponse(Profile profile) {
		return new ProfileResponse(
				profile.getId(),
				profile.getClerkUserId(),
				profile.getEmail(),
				profile.getDisplayName(),
				profile.getAvatarUrl(),
				profile.isOnboarded());
	}
}
