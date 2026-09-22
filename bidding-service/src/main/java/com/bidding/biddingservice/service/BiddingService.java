package com.bidding.biddingservice.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bidding.biddingservice.client.AuctionClient;
import com.bidding.biddingservice.client.HoldRequest;
import com.bidding.biddingservice.client.LeadingBidRequest;
import com.bidding.biddingservice.client.LeadingBidResponse;
import com.bidding.biddingservice.client.PaymentClient;
import com.bidding.biddingservice.client.ReleaseRequest;
import com.bidding.biddingservice.domain.Bid;
import com.bidding.biddingservice.repo.BidRepository;
import com.bidding.biddingservice.web.BidResponse;
import com.bidding.biddingservice.web.PlaceBidRequest;
import com.bidding.common.security.CurrentUser;
import com.bidding.common.web.ApiException;

@Service
public class BiddingService {

	private final BidRepository bids;
	private final AuctionClient auctions;
	private final PaymentClient payments;

	public BiddingService(BidRepository bids, AuctionClient auctions, PaymentClient payments) {
		this.bids = bids;
		this.auctions = auctions;
		this.payments = payments;
	}

	@Transactional
	public BidResponse place(PlaceBidRequest request) {
		String bidder = CurrentUser.clerkUserId();
		Bid bid = new Bid();
		bid.setAuctionId(request.auctionId());
		bid.setBidderClerkUserId(bidder);
		bid.setAmountCents(request.amountCents());
		bid.setStatus("rejected");
		bid = bids.saveAndFlush(bid);

		payments.hold(new HoldRequest(
				bidder,
				request.amountCents(),
				request.auctionId(),
				bid.getId(),
				"hold:" + bid.getId()));

		LeadingBidResponse result = auctions.leadingBid(
				request.auctionId(),
				new LeadingBidRequest(bidder, request.amountCents(), bid.getId()));
		if (!result.accepted()) {
			payments.release(new ReleaseRequest(bidder, request.auctionId(), bid.getId(), "release:" + bid.getId()));
			throw ApiException.conflict(result.code(), result.message());
		}

		bid.setStatus("accepted");
		bids.save(bid);

		if (result.previousBidId() != null && result.previousBidderId() != null) {
			payments.release(new ReleaseRequest(
					result.previousBidderId(),
					request.auctionId(),
					result.previousBidId(),
					"release:" + result.previousBidId()));
		}
		return BidResponse.from(bid);
	}

	@Transactional(readOnly = true)
	public List<BidResponse> mine() {
		return bids.findByBidderClerkUserIdOrderByCreatedAtDesc(CurrentUser.clerkUserId()).stream()
				.map(BidResponse::from)
				.toList();
	}
}
