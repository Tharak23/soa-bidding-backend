package com.bidding.auctionservice.web;

import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidding.auctionservice.service.AuctionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/internal/auctions")
public class InternalAuctionController {

	private final AuctionService auctions;

	public InternalAuctionController(AuctionService auctions) {
		this.auctions = auctions;
	}

	@PostMapping("/{id}/leading-bid")
	public LeadingBidResponse leadingBid(@PathVariable UUID id, @Valid @RequestBody LeadingBidRequest request) {
		return auctions.acceptLeadingBid(id, request);
	}
}
