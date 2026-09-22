package com.bidding.authservice.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bidding.authservice.domain.Profile;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

	Optional<Profile> findByClerkUserId(String clerkUserId);
}
