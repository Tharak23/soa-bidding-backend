package com.bidding.auctionservice.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bidding.auctionservice.domain.Bid;

public interface BidRepository extends JpaRepository<Bid, UUID> {

	List<Bid> findByAuctionIdOrderByCreatedAtDesc(UUID auctionId);
}
