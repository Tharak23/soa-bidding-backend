package com.bidding.auctionservice.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bidding.auctionservice.domain.Settlement;

public interface SettlementRepository extends JpaRepository<Settlement, UUID> {

	Optional<Settlement> findByAuctionId(UUID auctionId);

	boolean existsByAuctionId(UUID auctionId);
}
