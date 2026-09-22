package com.bidding.paymentservice.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bidding.paymentservice.domain.LedgerEntry;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {

	boolean existsByIdempotencyKey(String idempotencyKey);

	Optional<LedgerEntry> findByIdempotencyKey(String idempotencyKey);

	List<LedgerEntry> findByClerkUserIdOrderByCreatedAtDesc(String clerkUserId);

	List<LedgerEntry> findByAuctionIdAndEntryType(UUID auctionId, String entryType);
}
