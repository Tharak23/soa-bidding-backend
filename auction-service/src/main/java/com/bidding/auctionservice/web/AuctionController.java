package com.bidding.auctionservice.web;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidding.auctionservice.domain.Bid;
import com.bidding.auctionservice.service.AuctionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

	private final AuctionService auctions;

	public AuctionController(AuctionService auctions) {
		this.auctions = auctions;
	}

	@GetMapping
	public List<AuctionResponse> list() {
		return auctions.listOpen();
	}

	@PostMapping
	public AuctionResponse create(@Valid @RequestBody CreateAuctionRequest request) {
		return auctions.create(request);
	}

	@GetMapping("/{id}")
	public AuctionResponse get(@PathVariable UUID id) {
		return auctions.get(id);
	}

	@PostMapping("/{id}/cancel")
	public void cancel(@PathVariable UUID id) {
		auctions.cancel(id);
	}

	@GetMapping("/{id}/bids")
	public List<Bid> bids(@PathVariable UUID id) {
		return auctions.history(id);
	}
}
