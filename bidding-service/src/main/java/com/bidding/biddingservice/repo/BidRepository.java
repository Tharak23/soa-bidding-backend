package com.bidding.biddingservice.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bidding.biddingservice.domain.Bid;

public interface BidRepository extends JpaRepository<Bid, UUID> {

	List<Bid> findByBidderClerkUserIdOrderByCreatedAtDesc(String bidderClerkUserId);

	List<Bid> findByAuctionIdOrderByCreatedAtDesc(UUID auctionId);
}
