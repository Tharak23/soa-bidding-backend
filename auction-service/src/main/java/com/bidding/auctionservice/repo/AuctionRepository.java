package com.bidding.auctionservice.repo;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bidding.auctionservice.domain.Auction;
import com.bidding.auctionservice.domain.AuctionStatus;

import jakarta.persistence.LockModeType;

public interface AuctionRepository extends JpaRepository<Auction, UUID> {

	List<Auction> findByStatusOrderByEndsAtAsc(AuctionStatus status);

	List<Auction> findBySellerClerkUserIdOrderByCreatedAtDesc(String sellerClerkUserId);

	@Query("select a from Auction a where a.status = com.bidding.auctionservice.domain.AuctionStatus.OPEN and a.endsAt <= :now")
	List<Auction> findDue(@Param("now") Instant now);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select a from Auction a where a.id = :id")
	Optional<Auction> findByIdForUpdate(@Param("id") UUID id);

	@Modifying
	@Query("""
			update Auction a
			   set a.currentPriceCents = :amount,
			       a.leadingBidderClerkUserId = :bidder,
			       a.leadingBidId = :bidId
			 where a.id = :id
			   and a.status = com.bidding.auctionservice.domain.AuctionStatus.OPEN
			   and a.endsAt > :now
			   and a.currentPriceCents < :amount
			""")
	int acceptLeadingBid(
			@Param("id") UUID id,
			@Param("amount") long amount,
			@Param("bidder") String bidder,
			@Param("bidId") UUID bidId,
			@Param("now") Instant now);
}
