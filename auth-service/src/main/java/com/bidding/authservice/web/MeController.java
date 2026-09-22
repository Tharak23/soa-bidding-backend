package com.bidding.authservice.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidding.authservice.service.ProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/me")
public class MeController {

	private final ProfileService profiles;

	public MeController(ProfileService profiles) {
		this.profiles = profiles;
	}

	@GetMapping
	public ProfileResponse me() {
		return profiles.me();
	}

	@PatchMapping
	public ProfileResponse update(@Valid @RequestBody PatchProfileRequest request) {
		return profiles.update(request);
	}
}
