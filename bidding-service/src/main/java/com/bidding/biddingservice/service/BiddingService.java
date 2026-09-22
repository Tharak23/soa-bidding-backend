package com.bidding.biddingservice.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

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
	private final TransactionTemplate tx;

	public BiddingService(
			BidRepository bids,
			AuctionClient auctions,
			PaymentClient payments,
			PlatformTransactionManager transactionManager) {
		this.bids = bids;
		this.auctions = auctions;
		this.payments = payments;
		this.tx = new TransactionTemplate(transactionManager);
	}

	public BidResponse place(PlaceBidRequest request) {
		String bidder = CurrentUser.clerkUserId();
		Bid bid = Objects.requireNonNull(tx.execute(status -> {
			Bid created = new Bid();
			created.setAuctionId(request.auctionId());
			created.setBidderClerkUserId(bidder);
			created.setAmountCents(request.amountCents());
			created.setStatus("rejected");
			return bids.saveAndFlush(created);
		}));

		payments.hold(new HoldRequest(
				bidder,
				request.amountCents(),
				request.auctionId(),
				bid.getId(),
				"hold:" + bid.getId()));

		LeadingBidResponse result;
		try {
			result = auctions.leadingBid(
					request.auctionId(),
					new LeadingBidRequest(bidder, request.amountCents(), bid.getId()));
		} catch (RuntimeException ex) {
			payments.release(new ReleaseRequest(bidder, request.auctionId(), bid.getId(), "release:" + bid.getId()));
			throw ex;
		}
		if (!result.accepted()) {
			payments.release(new ReleaseRequest(bidder, request.auctionId(), bid.getId(), "release:" + bid.getId()));
			throw ApiException.conflict(result.code(), result.message());
		}

		Bid accepted = Objects.requireNonNull(tx.execute(status -> {
			Bid current = bids.findById(bid.getId()).orElseThrow();
			current.setStatus("accepted");
			return bids.save(current);
		}));

		if (result.previousBidId() != null && result.previousBidderId() != null) {
			payments.release(new ReleaseRequest(
					result.previousBidderId(),
					request.auctionId(),
					result.previousBidId(),
					"release:" + result.previousBidId()));
		}
		return BidResponse.from(accepted);
	}

	@Transactional(readOnly = true)
	public List<BidResponse> mine() {
		return bids.findByBidderClerkUserIdOrderByCreatedAtDesc(CurrentUser.clerkUserId()).stream()
				.map(BidResponse::from)
				.toList();
	}
}
