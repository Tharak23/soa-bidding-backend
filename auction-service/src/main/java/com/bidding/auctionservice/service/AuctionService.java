package com.bidding.auctionservice.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bidding.auctionservice.client.PaymentClient;
import com.bidding.auctionservice.client.SettleRequest;
import com.bidding.auctionservice.domain.Auction;
import com.bidding.auctionservice.domain.AuctionStatus;
import com.bidding.auctionservice.domain.Bid;
import com.bidding.auctionservice.domain.Settlement;
import com.bidding.auctionservice.repo.AuctionRepository;
import com.bidding.auctionservice.repo.BidRepository;
import com.bidding.auctionservice.repo.SettlementRepository;
import com.bidding.auctionservice.rules.AuctionSnapshot;
import com.bidding.auctionservice.rules.BidDecision;
import com.bidding.auctionservice.rules.BidRules;
import com.bidding.auctionservice.rules.CloseDecision;
import com.bidding.auctionservice.rules.CloseOutcome;
import com.bidding.auctionservice.web.AuctionResponse;
import com.bidding.auctionservice.web.CreateAuctionRequest;
import com.bidding.auctionservice.web.LeadingBidRequest;
import com.bidding.auctionservice.web.LeadingBidResponse;
import com.bidding.common.security.CurrentUser;
import com.bidding.common.web.ApiException;

@Service
public class AuctionService {

	private final AuctionRepository auctions;
	private final BidRepository bids;
	private final SettlementRepository settlements;
	private final PaymentClient payments;

	public AuctionService(
			AuctionRepository auctions,
			BidRepository bids,
			SettlementRepository settlements,
			PaymentClient payments) {
		this.auctions = auctions;
		this.bids = bids;
		this.settlements = settlements;
		this.payments = payments;
	}

	@Transactional
	public AuctionResponse create(CreateAuctionRequest request) {
		Auction auction = new Auction();
		auction.setSellerClerkUserId(CurrentUser.clerkUserId());
		auction.setTitle(request.title().trim());
		auction.setDescription(blankToNull(request.description()));
		auction.setCategory(blankToNull(request.category()));
		auction.setCondition(blankToNull(request.condition()));
		auction.setLocation(blankToNull(request.location()));
		auction.setImageUrl(blankToNull(request.imageUrl()));
		auction.setStartPriceCents(request.startPriceCents());
		auction.setMinIncrementCents(request.minIncrementCents());
		auction.setCurrentPriceCents(request.startPriceCents());
		auction.setStatus(AuctionStatus.OPEN);
		auction.setEndsAt(request.endsAt());
		return AuctionResponse.from(auctions.save(auction));
	}

	@Transactional(readOnly = true)
	public List<AuctionResponse> listOpen() {
		return auctions.findByStatusOrderByEndsAtAsc(AuctionStatus.OPEN).stream().map(AuctionResponse::from).toList();
	}

	@Transactional(readOnly = true)
	public AuctionResponse get(UUID id) {
		return AuctionResponse.from(auctions.findById(id).orElseThrow(() -> ApiException.notFound("auction_not_found", "Auction not found")));
	}

	@Transactional
	public void cancel(UUID id) {
		Auction auction = auctions.findById(id).orElseThrow(() -> ApiException.notFound("auction_not_found", "Auction not found"));
		if (!auction.getSellerClerkUserId().equals(CurrentUser.clerkUserId())) {
			throw ApiException.forbidden("not_seller", "Only the seller can cancel");
		}
		if (auction.getLeadingBidId() != null) {
			throw ApiException.conflict("has_bids", "Cannot cancel after a bid has been accepted");
		}
		if (auction.getStatus() != AuctionStatus.OPEN) {
			throw ApiException.conflict("not_open", "Auction is not open");
		}
		auction.setStatus(AuctionStatus.CLOSED);
	}

	@Transactional
	public LeadingBidResponse acceptLeadingBid(UUID auctionId, LeadingBidRequest request) {
		Auction auction = auctions.findByIdForUpdate(auctionId)
				.orElseThrow(() -> ApiException.notFound("auction_not_found", "Auction not found"));
		AuctionSnapshot snapshot = new AuctionSnapshot(
				auction.getStatus(),
				auction.getEndsAt(),
				auction.getSellerClerkUserId(),
				auction.getLeadingBidderClerkUserId(),
				auction.getStartPriceCents(),
				auction.getCurrentPriceCents(),
				auction.getMinIncrementCents());
		BidDecision decision = BidRules.evaluate(snapshot, request.bidderClerkUserId(), request.amountCents(), Instant.now());
		if (!decision.accepted()) {
			return new LeadingBidResponse(false, decision.code(), decision.message(), null, null);
		}
		String previousBidder = auction.getLeadingBidderClerkUserId();
		UUID previousBidId = auction.getLeadingBidId();
		int updated = auctions.acceptLeadingBid(
				auctionId,
				request.amountCents(),
				request.bidderClerkUserId(),
				request.bidId(),
				Instant.now());
		if (updated == 0) {
			return new LeadingBidResponse(false, "stale_price", "Another bid landed first", null, null);
		}
		return new LeadingBidResponse(true, "ok", "accepted", previousBidId, previousBidder);
	}

	@Transactional
	public void closeDueAuctions() {
		for (Auction due : auctions.findDue(Instant.now())) {
			closeOne(due.getId());
		}
	}

	@Transactional
	public void closeOne(UUID auctionId) {
		Auction auction = auctions.findByIdForUpdate(auctionId).orElse(null);
		if (auction == null || auction.getStatus() != AuctionStatus.OPEN) {
			return;
		}
		if (Instant.now().isBefore(auction.getEndsAt())) {
			return;
		}
		if (settlements.existsByAuctionId(auctionId)) {
			return;
		}
		auction.setStatus(AuctionStatus.CLOSING);
		auctions.flush();
		CloseOutcome outcome = CloseDecision.decide(auction.getLeadingBidderClerkUserId(), auction.getCurrentPriceCents());
		payments.settle(new SettleRequest(auctionId, outcome.winnerId(), auction.getLeadingBidId(), outcome.amountCents()));
		Settlement settlement = new Settlement();
		settlement.setAuctionId(auctionId);
		settlement.setWinnerClerkUserId(outcome.winnerId());
		settlement.setAmountCents(outcome.amountCents());
		settlements.save(settlement);
		for (Bid bid : bids.findByAuctionIdOrderByCreatedAtDesc(auctionId)) {
			if ("accepted".equals(bid.getStatus())) {
				boolean won = outcome.winnerId() != null && outcome.winnerId().equals(bid.getBidderClerkUserId())
						&& bid.getId().equals(auction.getLeadingBidId());
				bid.setStatus(won ? "won" : "lost");
			}
		}
		auction.setStatus(AuctionStatus.valueOf(outcome.status()));
	}

	@Transactional(readOnly = true)
	public List<Bid> history(UUID auctionId) {
		return bids.findByAuctionIdOrderByCreatedAtDesc(auctionId);
	}

	private static String blankToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}
